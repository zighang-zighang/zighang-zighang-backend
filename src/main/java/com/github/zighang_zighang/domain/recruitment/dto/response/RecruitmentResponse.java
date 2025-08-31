package com.github.zighang_zighang.domain.recruitment.dto.response;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Schema
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class RecruitmentResponse {

    @Schema(description = "공고 ID")
    String id;

    @Schema(description = "채용 공고 제목")
    String title;

    @Schema(description = "채용 공고 URL")
    String recruitmentUrl;

    @Schema(description = "공고 이미지 URL")
    String imageUrl;

    @Schema(description = "근무지 목록")
    List<Location> locations;

    @Schema(description = "최소 경력 (년)")
    Integer minExperience;

    @Schema(description = "최대 경력 (년)")
    Integer maxExperience;

    @Schema(description = "학력 조건 목록")
    List<Education> educations;

    @Schema(description = "채용 시작일")
    LocalDateTime startDate;

    @Schema(description = "채용 마감일")
    LocalDateTime endDate;

    @Schema(description = "마감 타입")
    DeadlineType deadlineType;

    @Schema(description = "고용 형태 목록")
    List<EmploymentType> employmentTypes;

    @Schema(description = "직무 목록")
    List<Job> jobs;

    @Schema(description = "직무 카테고리 목록")
    List<JobCategory> jobCategories;

    @Schema(description = "회사 이름")
    String companyName;

    @Schema(description = "회사 이미지 URL")
    String companyImageUrl;

    @Schema(description = "회사 규모")
    CompanySize companySize;

    public static RecruitmentResponse from(Recruitment recruitment) {

        return RecruitmentResponse.of(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getRecruitmentUrl(),
                recruitment.getImageUrl(),
                recruitment.getLocations(),
                recruitment.getMinExperience(),
                recruitment.getMaxExperience(),
                recruitment.getEducations(),
                recruitment.getStartDate(),
                recruitment.getEndDate(),
                recruitment.getDeadlineType(),
                recruitment.getEmploymentTypes(),
                recruitment.getJobs(),
                recruitment.getJobCategories(),
                recruitment.getCompanyName(),
                recruitment.getCompanyImageUrl(),
                recruitment.getCompanySize()
        );
    }
}
