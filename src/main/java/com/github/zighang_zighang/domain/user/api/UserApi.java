package com.github.zighang_zighang.domain.user.api;

import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.global.auth.service.CustomUserDetails;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "[유저]",
        description = "유저 API"
)
public interface UserApi {

    @Operation(
            summary = "온보딩 정보 등록/수정",
            description = "직군, 직무, 경력, 학력, 지역, 자기소개서 URL을 포함한 온보딩 정보를 등록하거나 수정합니다."
    )
    ApiResponse<UserResponse> addOnboarding(
            @Parameter(hidden = true) CustomUserDetails user,

            @Parameter(description = "유저 온보딩 요청 정보")
            UserOnboardingRequest userOnboardingRequest
    );
}
