package com.github.zighang_zighang.domain.recruitment.api;

import com.github.zighang_zighang.domain.recruitment.dto.request.RecruitmentSearchRequest;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

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
            HttpServletRequest request,

            @Parameter(hidden = true)
            User user,

            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );

    @Operation(
            summary = "공고 목록 조회",
            description = "조건으로 공고 목록을 필터링하여 조회합니다. 페이지네이션이 적용됩니다."
    )
    ApiResponse<PageResponse<RecruitmentResponse>> getRecruitments(
            @Parameter(hidden = true)
            User user,

            @Parameter(description = "공고 검색 및 필터링 조건")
            RecruitmentSearchRequest request
    );

    @Operation(
            summary = "지원하기 (로깅용)",
            description = "사용자가 특정 공고에 지원했음을 기록합니다."
    )
    ApiResponse<Void> logApplication(
            @Parameter(hidden = true)
            User user,

            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );
}
