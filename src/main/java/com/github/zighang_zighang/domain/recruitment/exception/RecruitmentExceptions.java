package com.github.zighang_zighang.domain.recruitment.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitmentExceptions implements ApiExceptionCode {

    NOT_FOUND("RECRUITMENT-001", "공고를 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}
