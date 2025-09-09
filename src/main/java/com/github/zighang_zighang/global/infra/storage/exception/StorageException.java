package com.github.zighang_zighang.global.infra.storage.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageException implements ApiExceptionCode {

    UPLOAD_FAILED("STORAGE-001", "파일 업로드에 실패했습니다.")
    ;

    private final String code;
    private final String message;

}
