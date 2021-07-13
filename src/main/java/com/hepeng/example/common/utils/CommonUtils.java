/*
 *                    .::::.
 *                  .::::::::.
 *                 :::::::::::  FUCK YOU
 *             ..:::::::::::'
 *           '::::::::::::'
 *             .::::::::::
 *        '::::::::::::::..
 *             ..::::::::::::.
 *           ``::::::::::::::::
 *            ::::``:::::::::'        .:::.
 *           ::::'   ':::::'       .::::::::.
 *         .::::'      ::::     .:::::::'::::.
 *        .:::'       :::::  .:::::::::' ':::::.
 *       .::'        :::::.:::::::::'      ':::::.
 *      .::'         ::::::::::::::'         ``::::.
 *  ...:::           ::::::::::::'              ``::.
 * ```` ':.            ':::::::::'                  ::::..
 *                    '.:::::'                    ':'````..
 *
 *
 *
 **************************************************************
 *                                                            *
 *   .=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.       *
 *    |                     ______                     |      *
 *    |                  .-"      "-.                  |      *
 *    |                 /            \                 |      *
 *    |     _          |              |          _     |      *
 *    |    ( \         |,  .-.  .-.  ,|         / )    |      *
 *    |     > "=._     | )(__/  \__)( |     _.=" <     |      *
 *    |    (_/"=._"=._ |/     /\     \| _.="_.="\_)    |      *
 *    |           "=._"(_     ^^     _)"_.="           |      *
 *    |               "=\__|IIIIII|__/="               |      *
 *    |              _.="| \IIIIII/ |"=._              |      *
 *    |    _     _.="_.="\          /"=._"=._     _    |      *
 *    |   ( \_.="_.="     `--------`     "=._"=._/ )    |      *
 *    |    > _.="                            "=._ <    |      *
 *    |   (_/                                    \_)   |      *
 *    |                                                |      *
 *    '-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-='      *
 *                                                            *
 *           LASCIATE OGNI SPERANZA, VOI CH'ENTRATE           *
 **************************************************************
 *
 *
 *
 *                    _ooOoo_
 *                   o8888888o
 *                   88" . "88
 *                   (| -_- |)
 *                   O\  =  /O
 *                ____/`---'\____
 *              .'  \\|     |//  `.
 *             /  \\|||  :  |||//  \
 *            /  _||||| -:- |||||-  \
 *            |   | \\\  -  /// |   |
 *            | \_|  ''\---/''  |   |
 *            \  .-\__  `-`  ___/-. /
 *          ___`. .'  /--.--\  `. . __
 *       ."" '<  `.___\_<|>_/___.'  >'"".
 *      | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *      \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                    `=---='
 *
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *            佛祖保佑       永无BUG
 *            心外无法       法外无心
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 */

/*
=======================================================
                .----.
             _.'__    `.
         .--(^)(^^)---/#\
       .' @          /###\
       :         ,   #####
        `-..__.-' _.-\###/
              `;_:    `"'
            .'"""""`.
           /,  ya ,\\
          //狗神保佑  \\
          `-._______.-'
          ___`. | .'___
         (______|______)
=======================================================
 */

package com.hepeng.example.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 通用开发工具类
 */
public final class CommonUtils {
    private CommonUtils(){}

    /**
     * 转为partitionid
     */
    public static long toPartitionid(long trailid, long appid){
        if(trailid>255 || appid>255){
            throw new IllegalArgumentException("trailid或appid不能大于255");
        }
        String trailidBinStr = Long.toBinaryString(trailid);
        trailidBinStr = leftPadUseZero(trailidBinStr, 8);
        return (trailid << (16 - trailidBinStr.length())) + appid;
    }


    /**
     * 解析partitionid
     */
    public static Map<String, Long> parsePartitionId(long partitionId){
        if(partitionId > 65535){
            throw new IllegalArgumentException("partitionId不能大于65535");
        }
        long trailid = partitionId >> 8;
        long appid = partitionId - (trailid << 8);
        Map<String, Long> dataMap = new HashMap<>();
        dataMap.put("trailid", trailid);
        dataMap.put("appid", appid);
        return dataMap;
    }


    public static long getAppid(long partitionId){
        return parsePartitionId(partitionId).get("appid");
    }


    public static long getTrailid(long partitionId){
        return parsePartitionId(partitionId).get("trailid");
    }


    /**
     * 金额元转分
     * @param amount 金额的元进制字符串
     * @return String 金额的分进制字符串
     */
    public static String yuanToFen(BigDecimal amount) {
        if (Objects.isNull(amount)) {
            return "0";
        }
        return String.valueOf(amount.multiply(new BigDecimal(100)).intValueExact());
    }


    /**
     * 金额分转元
     * @param amount 金额的分进制字符串
     * @return String 金额的元进制字符串（返回值固定两位小数且四舍五入，比如109.5会得到1.10）
     */
    public static BigDecimal fenToYuan(String amount) {
        if (StringUtils.isBlank(amount)) {
            return new BigDecimal(0);
        }
        return new BigDecimal(amount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 生成指定长度的，由纯数字组成的，随机字符串
     * <p>
     *     注意：返回的字符串的首字符，不会是零
     * </p>
     */
    public static String randomNumeric(final int count) {
        //RandomStringUtils这个类不推荐使用了，用RandomStringGenerator来代替
        //return RandomStringUtils.randomNumeric(count);
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').build();
        String str = generator.generate(count);
        while(str.startsWith("0")){
            str = generator.generate(count);
        }
        return str;
    }


    /**
     * 生成指定长度的，由纯小写字母组成的，随机字符串
     */
    public static String randomAlphabetic(final int count) {
        return new RandomStringGenerator.Builder().withinRange('a', 'z').build().generate(count);
    }


    /**
     * 获取Map中的属性
     * <p>
     *     由于Map.toString()打印出来的参数值对，是横着一排的...参数多的时候，不便于查看各个值
     *     故此仿照commons-lang3.jar中的{@link org.apache.commons.lang3.builder.ReflectionToStringBuilder#toString(Object)}编写了本方法
     * </p>
     * <p>
     *     目前只支持Map<String,String>、Map<String,String[]>、Map<String,byte[]>三种类型
     * </p>
     */
    public static String buildStringFromMap(Map<String, ?> map){
        if(null==map || map.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
        for(Map.Entry<String, ?> entry : map.entrySet()){
            sb.append("\n").append(entry.getKey()).append("=");
            //打印方式随值类型不同而不同
            Object value = entry.getValue();
            if(value instanceof String){
                sb.append(value);
            }
            if(value instanceof String[]){
                sb.append(Arrays.toString((String[])value));
            }
            if(value instanceof byte[]){
                sb.append(new String((byte[])value));
            }
        }
        return sb.append("\n]").toString();
    }


    /**
     * 字符串左补零
     * @param str    待补零的字符串
     * @param length 补零后的总长度
     * @return 假设str=3，size=4，则返回0003
     */
    public static String leftPadUseZero(String str, int length){
        char[] srcArray = str.toCharArray();
        char[] destArray = new char[length];
        Arrays.fill(destArray, '0');
        if(srcArray.length >= length){
            System.arraycopy(srcArray, 0, destArray, 0, length);
        }else{
            System.arraycopy(srcArray, 0, destArray, length-srcArray.length, srcArray.length);
        }
        return String.valueOf(destArray);
    }


    /**
     * 提取堆栈信息
     * <p>
     *     等价于{@link org.apache.commons.lang3.exception.ExceptionUtils#getStackTrace(Throwable)}
     * </p>
     */
    public static String extractStackTrace(Throwable cause){
        //ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        //cause.printStackTrace(new PrintStream(byteArrayOut));
        //return byteArrayOut.toString();
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            cause.printStackTrace(pw);
            return sw.toString();
        }
    }


    /**
     * 提取堆栈轨迹中的真实异常
     */
    public static String extractStackTraceCausedBy(Throwable cause){
        String allMsg = extractStackTrace(cause);
        if(allMsg.contains("Caused by: ")){
            allMsg = allMsg.substring(allMsg.lastIndexOf("Caused by: ") + 11);
        }
        if(allMsg.contains(": ")){
            allMsg = allMsg.substring(allMsg.indexOf(": ")+2, allMsg.indexOf("\n"));
        }else{
            allMsg = allMsg.substring(0, allMsg.indexOf("\n"));
        }
        if(allMsg.endsWith("\r")){
            allMsg = allMsg.substring(0, allMsg.length()-1);
        }
        return allMsg;
    }
}