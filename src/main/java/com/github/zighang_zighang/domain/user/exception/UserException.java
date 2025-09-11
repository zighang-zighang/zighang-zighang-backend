package com.github.zighang_zighang.domain.user.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserException implements ApiExceptionCode {

    NOT_FOUND("USER-001", "유저를 찾을 수 없습니다."),
    INVALID_JOB_CATEGORY_SELECTION("USER-002", "선택한 직무 중 일부가 선택된 직군에 속하지 않습니다."),
    EXCEEDED_MAX_JOB_SELECTION("USER-003", "선택 가능한 직군은 최대 3개까지입니다."),
    INVALID_CAREER_YEAR("USER-004", "올바르지 않은 경력입니다.")
    ;

    private final String code;
    private final String message;

}
