package com.github.zighang_zighang.global.auth.api;

import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(
        name = "[인증]",
        description = "인증 관련 API"
)
public interface AuthApi {

    @Operation(
            summary = "Access Token 재발급",
            description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.",
            parameters = {
                    @Parameter(
                            name = "Refresh-Token",
                            description = "요청 헤더에 포함된 Refresh Token",
                            required = true,
                            example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                    )
            }
    )
    ApiResponse<LoginResponse> refreshToken(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshTokenHeader
    );
}
