package com.github.zighang_zighang.global.infra.opensearch.exception;

import com.github.zighang_zighang.global.exception.ApiExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenSearchException implements ApiExceptionCode {

    EXTRACT_FAILED("OPENSEARCH-001", "OpenSearch에 임베딩을 인덱싱하는 도중 오류가 발생했습니다.")
    ;

    private final String code;
    private final String message;
}
