package com.github.zighang_zighang.domain.user.dto.request;

import com.github.zighang_zighang.global.classification.EducationLevel;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.classification.Region;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "경력")
    int careerYears;

    @Schema(description = "최종 학력")
    EducationLevel educationLevel;

    @Schema(description = "선호 근무지")
    Region preferredRegion;

}
