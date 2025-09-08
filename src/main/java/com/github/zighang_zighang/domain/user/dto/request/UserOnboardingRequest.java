package com.github.zighang_zighang.domain.user.dto.request;

import com.github.zighang_zighang.global.classification.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Schema
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class UserOnboardingRequest {

    @Schema(description = "선호 직군 (최대 3개)")
    List<Job> interestedJobs;

    @Schema(description = "선호 직무")
    List<JobCategory> interestedJobCategories;

    @Min(0)
    @Max(10)
    @Schema(description = "경력")
    int careerYear;

    @Schema(description = "최종 학력 - 최종 학교")
    EducationLevel educationLevel;

    @Schema(description = "최종 학력 - 졸업 구분")
    GraduationStatus graduationStatus;

    @Schema(description = "선호 근무지")
    Region preferredRegion;

}
