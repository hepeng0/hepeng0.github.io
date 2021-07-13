package com.hepeng.example.common.exception;

/**
 * description BaseErrorCode
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 16:14
 * @since 1.0
 */
public enum BaseErrorCode implements ErrorCode {
    INTERNAL_EXCEPTION("000001", "系统内部异常"),
    IP_FORBIDDEN("000002", "被禁止的IP"),
    IP_ACCESS_EXCEEDED("000003", "当前IP请求超过限制"),
    IP_FORMAT_ERROR("000004", "错误的IP地址"),
    IP_NULL_ERROR("000005", "IP地址不能为空"),
    UNKNOWN_REQUEST("000006", "未知的请求源"),
    REQUEST_EXCEEDED("000007", "请求超过次数限制"),
    API_ABANDONED("000008", "接口停用"),
    API_MAINTENANCE("000009", "接口维护"),
    API_VER_ERROR("000010", "版本号错误"),
    API_DOMAIN_ERROR("000011", "域名错误"),
    NETWORK_ERROR("000012", "网络错误"),
    UNKNOWN_ERROR("000013", "未知错误"),
    INTERNAL_ERROR("000014", "内部错误"),
    REQUEST_TIMEOUT("000015", "请求超时"),
    OUT_OF_MEMORY("000016", "内存溢出"),
    ILLEGAL_DATASOURCE("000017", "数据源异常"),
    USER_NOT_FOUND("000018", "用户不存在"),
    PERMISSION_DENIED("000019", "用户没有权限"),
    FREQUENT_OPERATION("000020", "操作频繁"),
    DATA_NOT_FOUND("000021", "无数据"),
    RESOURCE_DUPLICATED("000022", "资源已存在"),
    QUERIER_EMPTY_RESULT("000023", "查询无结果"),
    ID_REQUIRE("000024", "ID不能为空"),
    DATA_IN_UPDATING("000025", "数据更新中"),
    PARAMS_CHK_ERROR("000026", "参数校验错误"),
    MOBILE_FORMAT_ERROR("000027", "手机格式错误"),
    IDCARD_FORMAT_ERROR("000028", "身份证错误"),
    EMAIL_FORMAT_ERROR("000029", "邮箱错误"),
    LONGITUDE_LATITUDE_ERROR("000030", "经纬度错误"),
    REQUEST_PATH_ERROR("000031", "请求路径错误"),
    DATA_FORMAT_ERROR("000032", "数据格式错误"),
    TIME_PARAMS_ERROR("000033", "时间参数错误"),
    CAR_LICENSE_ERROR("000034", "车牌号错误"),
    CAMERA_NUMBER_ERROR("000035", "摄像头编号错误"),
    CALLBACK_ADDR_ERROR("000036", "回调地址URL错误"),
    TIME_FORMAT_ERROR("000037", "时间格式必须是ISO-8601"),
    PAGE_NUM_OVERLIMIT("000038", "页码超出限制"),
    PAGE_SIZE_OVERLIMIT("000039", "分页大小超出限制"),
    API_NOT_FOUND("000040", "接口不存在"),
    DECRYPTION_FAILURE("000041", "解密失败"),
    NECESSARY_PARAMS_REQURIED("000042", "缺少必要参数"),
    DOWNLOAD_FAILURE("000043", "下载失败"),
    UNIDENTIFIED_FILE("000044", "未识别文件"),
    FILE_SIZE_MAXIMUM("000045", "文件大小超过限制"),
    FILE_UPLOAD_FAILURE("000046", "文件上传失败"),
    FILE_NOT_FOUND("000047", "文件不存在"),
    ACCOUNT_ERROR("000048", "账号密码错误"),
    Test("000048", "账号密码错误");

    private String errorCode;
    private String message;

    BaseErrorCode(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
