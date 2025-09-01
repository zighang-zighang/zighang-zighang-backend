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
    AUTHENTICATION_FAILED("AUTH-005", "인증에 실패했습니다."),
    EMAIL_NOT_PROVIDED("AUTH-006", "OAuth2 제공자로부터 이메일 정보를 가져올 수 없습니다. 이메일 동의가 필요합니다."),
    REFRESH_TOKEN_NOT_FOUND("AUTH-007", "Refresh token을 찾을 수 없습니다."),
    PROVIDER_ID_NOT_FOUND("AUTH-008", "제공자 ID를 찾을 수 없습니다."),
    PROVIDER_TYPE_NOT_FOUND("AUTH-009", "제공자 타입을 찾을 수 없습니다."),
    USER_NOT_FOUND("AUTH-010", "사용자를 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN("AUTH-011", "유효하지 않은 refresh token입니다.");

    private final String code;
    private final String message;
}
