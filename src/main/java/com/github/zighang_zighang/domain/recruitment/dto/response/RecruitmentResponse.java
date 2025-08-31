package com.github.zighang_zighang.domain.recruitment.dto.response;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.entity.recruitment.Experience;
import com.github.zighang_zighang.domain.recruitment.entity.recruitment.JobPosition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Schema
@Getter
@AllArgsConstructor(staticName = "of")
public class RecruitmentResponse {

    @Schema(description = "회사 이름")
    String companyName;

    @Schema(description = "채용 공고 제목")
    String title;

    @Schema(description = "직무")
    List<JobPositionDto> jobPositions;

    @Schema(description = "채용 시작일")
    LocalDateTime recruitmentStart;

    @Schema(description = "채용 마감일")
    LocalDateTime recruitmentEnd;

    @Schema(description = "마감 타입")
    EndType endType;

    @Schema(description = "근무지")
    Location location;

    @Schema(description = "학력")
    Education education;

    @Schema(description = "경력")
    ExperienceDto experience;

    @Schema(description = "고용 형태")
    EmploymentType employmentType;

    @Schema(description = "회사 규모")
    CompanySize companySize;

    public static RecruitmentResponse from(Recruitment recruitment) {

        return RecruitmentResponse.of(
                recruitment.getCompanyName(),
                recruitment.getTitle(),
                recruitment.getJobPositions().stream().map(JobPositionDto::from).toList(),
                recruitment.getRecruitmentStart(),
                recruitment.getRecruitmentEnd(),
                recruitment.getEndType(),
                recruitment.getLocation(),
                recruitment.getEducation(),
                ExperienceDto.from(recruitment.getExperience()),
                recruitment.getEmploymentType(),
                recruitment.getCompanySize()
        );
    }

    @Schema
    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class JobPositionDto {

        @Schema(description = "직무")
        Job job;

        @Schema(description = "직무 그룹")
        JobGroup jobGroup;

        public static JobPositionDto from(JobPosition jobPosition) {

            return JobPositionDto.of(
                    jobPosition.getJob(),
                    jobPosition.getJobGroup()
            );
        }
    }

    @Schema
    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class ExperienceDto {

        @Schema(description = "최소 경력 (년)")
        Integer minExperience;

        @Schema(description = "최대 경력 (년)")
        Integer maxExperience;

        public static ExperienceDto from(Experience experience) {

            return ExperienceDto.of(
                    experience.getMinExperience(),
                    experience.getMaxExperience()
            );
        }
    }
}
