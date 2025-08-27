package com.github.zighang_zighang.global.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
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