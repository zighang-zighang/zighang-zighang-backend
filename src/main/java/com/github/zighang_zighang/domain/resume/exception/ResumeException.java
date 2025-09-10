package com.github.zighang_zighang.domain.resume.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResumeException implements ApiExceptionCode {

    NOT_FOUND("RESUME-001", "Resume를 찾을 수 없습니다."),
    FORBIDDEN("RESUME-002", "본인 이력서/자기소개서만 삭제할 수 있습니다.")
    ;

    private final String code;
    private final String message;
}
