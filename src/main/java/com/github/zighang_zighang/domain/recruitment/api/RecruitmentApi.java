package com.github.zighang_zighang.domain.recruitment.api;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );

    @Operation(
            summary = "공고 목록 조회 (필터링)",
            description = "다양한 조건으로 공고 목록을 필터링하여 조회합니다. 페이지네이션이 적용됩니다."
    )
    ApiResponse<Page<RecruitmentResponse>> getRecruitments(
            @Parameter(description = "직무 필터 (복수 선택 가능)")
            List<Job> jobs,

            @Parameter(description = "직군 필터 (복수 선택 가능)")
            List<JobGroup> jobGroups,

            @Parameter(description = "고용 형태 필터 (복수 선택 가능)")
            List<EmploymentType> employmentTypes,

            @Parameter(description = "학력 필터 (복수 선택 가능)")
            List<Education> educations,

            @Parameter(description = "경험 최소값")
            Integer minExperience,

            @Parameter(description = "경험 최대값")
            Integer maxExperience,

            @Parameter(description = "지역 필터 (복수 선택 가능)")
            List<Location> locations,

            @Parameter(description = "마감 유형 필터 (복수 선택 가능)")
            List<EndType> endTypes,

            @Parameter(description = "페이지 정보 (page, size, sort)")
            Pageable pageable
    );
}
