package com.github.zighang_zighang.domain.recruitment.api;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;
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
            User user,

            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );

    @Operation(
            summary = "공고 목록 조회",
            description = "조건으로 공고 목록을 필터링하여 조회합니다. 페이지네이션이 적용됩니다."
    )
    ApiResponse<PageResponse<RecruitmentResponse>> getRecruitments(
            @Parameter(description = "직무 필터 (복수 선택 가능)")
            List<Job> jobs,

            @Parameter(description = "직군 필터 (복수 선택 가능)")
            List<JobCategory> jobCategories,

            @Parameter(description = "고용 형태 필터 (복수 선택 가능)")
            List<EmploymentType> employmentTypes,

            @Parameter(description = "학력 필터 (복수 선택 가능)")
            List<Education> educations,

            @Min(0)
            @Max(10)
            @Parameter(description = "경험 최소값")
            Integer minExperience,

            @Min(0)
            @Max(10)
            @Parameter(description = "경험 최대값")
            Integer maxExperience,

            @Parameter(description = "지역 필터 (복수 선택 가능)")
            List<Location> locations,

            @Parameter(description = "마감 유형 필터 (복수 선택 가능)")
            List<DeadlineType> deadlineTypes,

            @Min(0)
            @Parameter(description = "페이지")
            Integer page,

            @Min(1)
            @Max(100)
            @Parameter(description = "페이지 크기")
            Integer size
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
