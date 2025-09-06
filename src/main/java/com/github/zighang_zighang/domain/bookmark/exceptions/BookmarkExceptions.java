package com.github.zighang_zighang.domain.bookmark.exceptions;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookmarkExceptions implements ApiExceptionCode {

    NOT_FOUND("BOOKMARK-001", "북마크를 찾을 수 없습니다."),
    ALREADY_ADDED("BOOKMARK-002", "이미 북마크에 추가된 공고입니다."),
    ;

    private final String code;
    private final String message;
}
