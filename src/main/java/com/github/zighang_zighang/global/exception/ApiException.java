package com.github.zighang_zighang.global.exception;

import com.github.zighang_zighang.global.response.ApiResponse;

public class ApiException extends RuntimeException {

    private final ApiExceptionCode code;

    public ApiException(ApiExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public String getErrorCode() {
        return code.getCode();
    }

    public String getErrorMessage() {
        return code.getMessage();
    }

    public ApiResponse<?> toResponse() {
        return code.toResponse();
    }
}