package com.github.zighang_zighang.domain.recruitment.entity;

import com.github.zighang_zighang.domain.recruitment.constant.CompanySize;
import com.github.zighang_zighang.domain.recruitment.constant.DeadlineType;
import com.github.zighang_zighang.domain.recruitment.constant.Education;
import com.github.zighang_zighang.domain.recruitment.constant.EmploymentType;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.classification.Location;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment {

    @Nonnull
    UUID id;

    @Nonnull
    String title;

    @Nullable
    String description;

    @Nonnull
    String recruitmentUrl;

    @Nonnull
    String imageUrl;

    @Nonnull
    List<Location> locations;

    @Nullable
    Integer minExperience;

    @Nullable
    Integer maxExperience;

    @Nonnull
    List<Education> educations;

    @Nullable
    LocalDateTime startDate;

    @Nullable
    LocalDateTime endDate;

    @Nonnull
    DeadlineType deadlineType;

    @Nonnull
    List<EmploymentType> employmentTypes;

    @Nonnull
    List<Job> jobs;

    @Nonnull
    List<JobCategory> jobCategories;

    @Nonnull
    String companyName;

    @Nullable
    String companyDescription;

    @Nullable
    String companyImageUrl;

    @Nonnull
    CompanySize companySize;

    @Nonnull
    List<Double> vector;
}