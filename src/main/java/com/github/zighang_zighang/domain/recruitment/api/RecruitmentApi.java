package com.github.zighang_zighang.domain.recruitment.api;

import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(
        name = "[공고]",
        description = "공고 API"
)
public interface RecruitmentApi {

    @Operation(
            summary = "공고 상세 조회",
            description = "공고 ID를 통해 특정 공고의 상세 정보를 조회합니다."
    )
    ApiResponse<RecruitmentResponse> getRecruitment(
            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );
}
