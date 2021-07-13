package com.hepeng.example.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * description BizException
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 16:09
 * @since 1.0
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    private int httpStatus = 500;
    private Object body;
    protected String errorCode;

    public BizException(String message, Throwable cause, int httpStatus, String errorCode, Object body) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.body = body;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getErrorCode();
    }

    public BizException(ErrorCode errorCode, Object... params) {
        super(String.format(errorCode.getMessage(), params));
        this.errorCode = errorCode.getErrorCode();
    }

    public BizException(ErrorCode errorCode, Throwable cause, Object... params) {
        super(String.format(errorCode.getMessage(), params), cause);
        this.errorCode = errorCode.getErrorCode();
    }

    public static <T> BizException.BizExceptionBuilder<T> builder() {
        return new BizException.BizExceptionBuilder();
    }

    /**
     * of构造器
     */
    public static BizException of(ErrorCode errorCode) {
        return new BizException(errorCode);
    }

    /**
     * 带参of构造器
     * @param params 参数
     */
    public static BizException of(ErrorCode errorCode, Object... params) {
        return new BizException(errorCode, params);
    }

    /**
     * builder构造器
     * @param <T> body类型
     */
    public static class BizExceptionBuilder<T> {
        private ErrorCode errorCode;
        private Object[] params;
        private Throwable cause;
        private int status;
        private T body;

        protected BizExceptionBuilder() {
        }

        public BizException.BizExceptionBuilder<T> errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public BizException.BizExceptionBuilder<T> params(Object... params) {
            this.params = params;
            return this;
        }

        public BizException.BizExceptionBuilder<T> cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public BizException.BizExceptionBuilder<T> status(int status) {
            this.status = status;
            return this;
        }

        public BizException.BizExceptionBuilder<T> status(T body) {
            this.body = body;
            return this;
        }

        public BizException build() {
            return new BizException(String.format(this.errorCode.getMessage(), this.params), this.cause,
                    this.status, this.errorCode.getErrorCode(), this.body);
        }
    }


    private static String replace(String message, Object... values) {
        if (values != null) {
            Object[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Object val = var2[var4];
                if (message.contains("{}")) {
                    message = message.replaceFirst("\\{}", val.toString());
                }
            }
        }

        return message;
    }

}
