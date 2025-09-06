package com.github.zighang_zighang.domain.memo.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemoExceptions implements ApiExceptionCode {

    NOT_FOUND("MEMO-001", "메모를 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}