package com.github.zighang_zighang.global.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiExceptionCode code;

    public ApiException(ApiExceptionCode code) {

        super(code.getMessage());
        this.code = code;
    }
}