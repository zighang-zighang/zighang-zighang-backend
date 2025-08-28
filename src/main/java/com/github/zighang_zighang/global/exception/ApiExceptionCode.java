package com.github.zighang_zighang.global.exception;

import com.github.zighang_zighang.global.response.ApiResponse;

public interface ApiExceptionCode {

    String getCode();
    String getMessage();

    default ApiException toException() {

        return new ApiException(this);
    }

    default ApiResponse<?> toResponse() {

        return ApiResponse.error(getCode(), getMessage());
    }
}