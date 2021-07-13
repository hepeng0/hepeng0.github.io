package com.hepeng.example.common.constants;

public class CommonConstants {
    /**
     * 私有化构造器
     */
    private CommonConstants() {
        throw new IllegalStateException(PRIVATE_CONSTRUCTOR);
    }

    /**
     * 工具类不能被实例化异常提示
     */
    public static final String PRIVATE_CONSTRUCTOR = "工具类不能被实例化";

    /**
     * 项目产品名称
     */
    public static final String APPLICATION_NAME = "example";

    // 图片验证码
    public static final String IMG_CAPTCHA = "captcha";
    public static final String IMG_SRC_BASE64 = "imgSrcBase64";
}
