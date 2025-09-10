package com.github.zighang_zighang.global.infra.storage.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageException implements ApiExceptionCode {

    UPLOAD_FAILED("STORAGE-001", "파일 업로드에 실패했습니다."),
    INVALID_FILE_EXTENSION("STORAGE-002", "허용되지 않은 파일 형식입니다."),
    UNKNOWN_FILE_EXTENSION("STORAGE-003", "파일 형식을 확인할 수 없습니다."),
    FILE_REQUIRED("STORAGE-004", "파일이 누락되었습니다."),
    EMPTY_FILE("STORAGE-005", "빈 파일은 업로드할 수 없습니다."),
    FILE_SIZE_EXCEEDED("STORAGE-006", "파일 크기가 초과되었습니다. 최대 10MB까지 업로드할 수 있습니다."),
    DELETE_FAILED("STORAGE-007", "파일 삭제에 실패했습니다.")
    ;

    private final String code;
    private final String message;

}
