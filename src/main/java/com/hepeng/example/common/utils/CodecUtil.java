package com.hepeng.example.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 加解密工具类
 * -----------------------------------------------------------------------------------------------------------
 * @version v1.9
 * @history v1.9-->修改RSA、AES、DESede、DES算法的方法名，使之简洁易懂
 * @history v1.8-->修复buildAESPKCS7Decrypt()方法没有初始化BouncyCastleProvider的BUG
 * @history v1.7-->细化各方法参数注释，使之描述更清晰
 * @history v1.6-->RSA算法加解密方法增加分段加解密功能，理论上可加解密任意长度的明文或密文
 * @history v1.5-->增加RSA算法加解密及签名验签的方法
 * @history v1.4-->增加AES-PKCS7算法加解密数据的方法
 * @history v1.3-->增加buildHMacSign()的签名方法，目前支持<code>HMacSHA1,HMacSHA256,HMacSHA512,HMacMD5</code>算法
 * @history v1.2-->修改buildHexSign()方法，取消用于置顶返回字符串大小写的第四个参数，修改后默认返回大写字符串
 * @history v1.1-->增加AES,DES,DESede等算法的加解密方法
 * @history v1.0-->新增buildHexSign()的签名方法，目前支持<code>MD5,SHA,SHA1,SHA-1,SHA-256,SHA-384,SHA-512</code>算法
 * -----------------------------------------------------------------------------------------------------------
 */
public final class CodecUtil {
    //密钥算法
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_RSA_SIGN = "SHA256WithRSA";
    public static final int ALGORITHM_RSA_PRIVATE_KEY_LENGTH = 2048;
    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES_PKCS7 = "AES";
    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_DES_EDE = "DESede";
    //加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
    //工作模式有ECB--电子密码本模式,CBC--加密分组链接模式,CFB--加密反馈模式,OFB--输出反馈模式,CTR--计数器模式
    //其中ECB过于简单而不安全,已被弃用,相对的CBC模式是最安全的,http://www.moye.me/2015/06/14/cryptography_rsa/
    private static final String ALGORITHM_CIPHER_AES = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM_CIPHER_AES_PKCS7 = "AES/CBC/PKCS7Padding";
    private static final String ALGORITHM_CIPHER_DES = "DES/ECB/PKCS5Padding";
    private static final String ALGORITHM_CIPHER_DES_EDE = "DESede/ECB/PKCS5Padding";

    private CodecUtil(){}

    /**
     * 生成AES/CBC/PKCS7Padding专用的IV
     * ECB模式只用密钥即可对数据进行加解密,CBC模式需要添加一个参数IV
     * IV是一个16字节的数组,这里采用和IOS一样的构造方法,数据全为0
     */
    private static AlgorithmParameters initIV(){
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte)0x00);
        AlgorithmParameters params;
        try {
            params = AlgorithmParameters.getInstance(ALGORITHM_AES_PKCS7);
            params.init(new IvParameterSpec(iv));
        } catch (Exception e) {
            throw new IllegalArgumentException("生成"+ALGORITHM_CIPHER_AES_PKCS7+"专用的IV时失败", e);
        }
        return params;
    }


    /**
     * 初始化算法密钥
     * 目前algorithm参数可选值为AES,DES,DESede,输入其它值时会抛异常
     * 若系统无法识别algorithm会导致实例化密钥生成器失败,也会抛异常
     * @param algorithm      指定生成哪种算法的密钥
     * @param isPKCS7Padding 是否采用PKCS7Padding填充方式(需要BouncyCastle支持)
     * @return 经过Base64编码后的密钥字符串,对于AES-PKCS7Padding算法则返回16进制表示的密钥字符串
     */
    public static String initKey(String algorithm, boolean isPKCS7Padding){
        if(isPKCS7Padding){
            Security.addProvider(new BouncyCastleProvider());
        }
        //实例化密钥生成器
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + algorithm + "]");
        }
        //初始化密钥生成器:AES要求密钥长度为128,192,256位
        if(ALGORITHM_AES.equals(algorithm)){
            kg.init(128);
        // }else if(ALGORITHM_AES_PKCS7.equals(algorithm)){
        //     kg.init(128);
        }else if(ALGORITHM_DES.equals(algorithm)){
            kg.init(56);
        }else if(ALGORITHM_DES_EDE.equals(algorithm)){
            kg.init(168);
        }else{
            throw new IllegalArgumentException("Not supported algorithm-->[" + algorithm + "]");
        }
        //生成密钥
        SecretKey secretKey = kg.generateKey();
        //获取二进制密钥编码形式
        if(isPKCS7Padding){
            return Hex.encodeHexString(secretKey.getEncoded());
        }
        return Base64.encodeBase64URLSafeString(secretKey.getEncoded());
    }


    /**
     * 初始化RSA算法密钥对
     * @param keysize RSA1024已经不安全了，建议2048
     * @return 经过Base64编码后的公私钥Map，键名分别为publicKey和privateKey
     */
    public static Map<String, String> initRSAKey(int keysize){
        if(keysize != ALGORITHM_RSA_PRIVATE_KEY_LENGTH){
            throw new IllegalArgumentException("RSA1024已经不安全了，请使用"+ALGORITHM_RSA_PRIVATE_KEY_LENGTH+"初始化RSA密钥对");
        }
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + ALGORITHM_RSA + "]");
        }
        //初始化KeyPairGenerator对象,不要被initialize()源码表面上欺骗,其实这里声明的size是生效的
        kpg.initialize(ALGORITHM_RSA_PRIVATE_KEY_LENGTH);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        return keyPairMap;
    }


    /**
     * RSA算法分段加解密数据
     * @param cipher 初始化了加解密工作模式后的javax.crypto.Cipher对象
     * @param opmode 加解密模式，值为javax.crypto.Cipher.ENCRYPT_MODE/DECRYPT_MODE
     * @param datas   待分段加解密的数据的字节数组
     * @return 加密或解密后得到的数据的字节数组
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas){
        int maxBlock;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8;
        }else{
            maxBlock = ALGORITHM_RSA_PRIVATE_KEY_LENGTH / 8 - 11;
        }
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            int offSet = 0;
            byte[] buff;
            int i = 0;
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            return out.toByteArray();
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
    }


    /**
     * RSA算法公钥加密数据
     * @param data 待加密的明文字符串
     * @param key  RSA公钥字符串
     * @return RSA公钥加密后的经过Base64编码的密文字符串
     */
    public static String rsaEncrypt(String data, String key){
        try{
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            //encrypt
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(CHARSET)));
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * RSA算法私钥解密数据
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥解密后的明文字符串
     */
    public static String rsaDecrypt(String data, String key){
        try{
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //decrypt
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //return new String(cipher.doFinal(Base64.decodeBase64(data)), CHARSET);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data)), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * RSA算法私钥签名数据
     * 签名算法SHA1WithRSA已被废弃，推荐使用SHA256WithRSA
     * @param data 待签名的明文字符串
     * @param key  RSA私钥字符串
     * @return RSA私钥签名后的经过Base64编码的字符串
     */
    public static String rsaSign(String data, String key){
        try{
            //通过PKCS#8编码的Key指令获得私钥对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //sign
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64URLSafeString(signature.sign());
        }catch(Exception e){
            throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * RSA算法公钥验签数据
     * @param data 参与签名的明文字符串
     * @param key  RSA公钥字符串
     * @param sign RSA签名得到的经过Base64编码的字符串
     * @return true--验签通过,false--验签未通过
     */
    public static boolean rsaVerify(String data, String key, String sign){
        try{
            //通过X509编码的Key指令获得公钥对象
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
            //verify
            Signature signature = Signature.getInstance(ALGORITHM_RSA_SIGN);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign));
        }catch(Exception e){
            throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES算法加密数据
     * @param data 待加密的明文数据
     * @param key  AES密钥字符串
     * @return AES加密后的经过Base64编码的密文字符串,加密过程中遇到异常则抛出RuntimeException
     * */
    public static String buildAESEncrypt(String data, String key){
        try{
            //实例化Cipher对象,它用于完成实际的加密操作
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
            //还原密钥,并初始化Cipher对象,设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
            //执行加密操作,加密后的结果通常都会用Base64编码进行传输
            //将Base64中的URL非法字符如'+','/','='转为其他字符,详见RFC3548
            return Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES算法解密数据
     * @param data 待解密的经过Base64编码的密文字符串
     * @param key  AES密钥字符串
     * @return AES解密后的明文字符串,解密过程中遇到异常则抛出RuntimeException
     * */
    public static String buildAESDecrypt(String data, String key){
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM_AES));
            return new String(cipher.doFinal(Base64.decodeBase64(data)), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES-PKCS7算法加密数据
     * 兼容IOS中的SecKeyWrapper加解密（SecKeyWrapper采用的是PKCS7Padding填充方式）
     * @param data 待加密的明文字符串
     * @param key  AES密钥字符串
     * @return AES-PKCS7加密后的16进制表示的密文字符串，加密过程中遇到异常则抛出RuntimeException
     */
    public static String buildAESPKCS7Encrypt(String data, String key){
        Security.addProvider(new BouncyCastleProvider());
        try{
            SecretKey secretKey = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), ALGORITHM_AES_PKCS7);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, initIV());
            return Hex.encodeHexString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    /**
     * AES-PKCS7算法解密数据
     * 兼容IOS中的SecKeyWrapper加解密（SecKeyWrapper采用的是PKCS7Padding填充方式）
     * @param data 待解密的16进制表示的密文字符串
     * @param key  AES密钥字符串
     * @return AES-PKCS7解密后的明文字符串，解密过程中遇到异常则抛出RuntimeException
     */
    public static String buildAESPKCS7Decrypt(String data, String key){
        Security.addProvider(new BouncyCastleProvider());
        try {
            SecretKey secretKey = new SecretKeySpec(Hex.decodeHex(key.toCharArray()), ALGORITHM_AES_PKCS7);
            Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER_AES_PKCS7);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initIV());
            return new String(cipher.doFinal(Hex.decodeHex(data.toCharArray())), StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }
}