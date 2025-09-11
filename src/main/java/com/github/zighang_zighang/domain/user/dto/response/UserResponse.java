package com.github.zighang_zighang.domain.user.dto.response;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.classification.EducationLevel;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.classification.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Schema
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class UserResponse {

    @Schema(description = "사용자 ID")
    UUID id;

    @Schema(description = "사용자 email")
    String email;

    @Schema(description = "사용자 이름")
    String name;

    @Schema(description = "선호 직군 (최대 3개)")
    List<Job> interestedJobs;

    @Schema(description = "선호 직무")
    List<JobCategory> interestedJobCategories;

    @Schema(description = "경력")
    int careerYear;

    @Schema(description = "최종 학력")
    EducationLevel educationLevel;

    @Schema(description = "선호 근무지")
    Location preferredRegion;

    public static UserResponse from(User user) {

        return UserResponse.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getInterestedJobs(),
                user.getInterestJobCategories(),
                user.getCareerYear(),
                user.getEducationLevel(),
                user.getPreferredRegion()
        );
    }

}
