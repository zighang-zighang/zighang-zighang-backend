package com.github.zighang_zighang.domain.user.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserException implements ApiExceptionCode {

    NOT_FOUND("USER-001", "유저를 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;

}
