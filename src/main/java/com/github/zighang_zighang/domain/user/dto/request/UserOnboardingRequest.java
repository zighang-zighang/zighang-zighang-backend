package com.github.zighang_zighang.domain.user.dto.request;

import com.github.zighang_zighang.global.classification.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Schema
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class UserOnboardingRequest {

    @NotNull
    @Size(min = 1, max = 3)
    @Schema(description = "선호 직군 (최대 3개)")
    List<Job> interestedJobs;

    @NotNull
    @Schema(description = "선호 직무")
    List<JobCategory> interestedJobCategories;

    @Min(0)
    @Max(10)
    @NotNull
    @Schema(description = "경력")
    int careerYear;

    @NotNull
    @Schema(description = "최종 학력 - 최종 학교")
    EducationLevel educationLevel;

    @NotNull
    @Schema(description = "최종 학력 - 졸업 구분")
    GraduationStatus graduationStatus;

    @NotNull
    @Schema(description = "선호 근무지")
    Region preferredRegion;

}
