package com.github.zighang_zighang.global.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiExceptionCode code;

    public ApiException(ApiExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }
    
    /**
     * 에러 코드를 String으로 반환 (API 응답용)
     */
    public String getErrorCode() {
        return code.getCode();
    }
    
    /**
     * 에러 메시지를 String으로 반환 (API 응답용)
     */
    public String getErrorMessage() {
        return code.getMessage();
    }
}