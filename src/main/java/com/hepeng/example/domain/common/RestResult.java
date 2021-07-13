package com.hepeng.example.domain.common;

import com.hepeng.example.common.exception.BaseErrorCode;
import com.hepeng.example.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestResult<T> {
    private String errorCode = "0000000000";
    private String message = "接口响应成功";
    private T data;

    public RestResult(T data) {
        this.data = data;
    }

    private RestResult(String errorCode, String msg) {
        this.errorCode = errorCode;
        this.message = msg;
    }

    public static <Void> RestResult<Void> fail(String code, String message) {
        return new RestResult<>(code, message);
    }

    public static <Void> RestResult<Void> fail(ErrorCode errorCode) {
        return new RestResult<>(errorCode.getErrorCode(), errorCode.getMessage());
    }


    public static <Void> RestResult<Void> fail() {
        return fail(BaseErrorCode.INTERNAL_EXCEPTION);
    }

    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(data);
    }

    public static <Void> RestResult<Void> success() {
        return new RestResult<>();
    }
}
