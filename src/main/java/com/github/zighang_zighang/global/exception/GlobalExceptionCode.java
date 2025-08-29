package com.github.zighang_zighang.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionCode implements ApiExceptionCode {

    NOT_FOUND("GLOBAL-001", "요청하신 리소스를 찾을 수 없습니다."),
    NOT_PERMITTED("GLOBAL-002", "권한이 없습니다."),
    BODY_NOT_READABLE("GLOBAL-003", "요청 데이터가 올바르지 않습니다."),
    BODY_VALIDATION_FAILED("GLOBAL-004", "요청 데이터가 올바르지 않습니다."),
    EXCEPTION("GLOBAL-010", "서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
    ;

    private final String code;
    private final String message;
}
