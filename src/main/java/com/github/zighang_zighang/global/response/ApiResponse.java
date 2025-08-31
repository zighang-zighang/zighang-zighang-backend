package com.github.zighang_zighang.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "에러 코드", example = "null")
    private final String code;

    @Schema(description = "에러 메시지", example = "null")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    public static ApiResponse<Void> ok() {

        return ok(null);
    }

    public static <T> ApiResponse<T> ok(T data) {

        return new ApiResponse<>(true, null, null, data);
    }

    public static <T> ApiResponse<T> error(String code, String message) {

        return new ApiResponse<>(false, code, message, null);
    }
}