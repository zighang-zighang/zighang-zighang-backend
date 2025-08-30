package com.github.zighang_zighang.global.auth.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements ApiExceptionCode {

    OAUTH2_FAILURE("AUTH-001", "OAuth2 인증에 실패했습니다."),
    TOKEN_NOT_FOUND("AUTH-002", "토큰을 찾을 수 없습니다."),
    TOKEN_EXPIRED("AUTH-003", "토큰이 만료되었습니다."),
    INVALID_TOKEN("AUTH-004", "유효하지 않은 토큰입니다."),
    AUTHENTICATION_FAILED("AUTH-005", "인증에 실패했습니다.");

    private final String code;
    private final String message;
}
