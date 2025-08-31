package com.github.zighang_zighang.domain.recruitment.entity;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment {

    @Nonnull
    String id;

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
    EndType deadlineType;

    @Nonnull
    List<EmploymentType> employmentTypes;

    @Nonnull
    List<Job> jobs;

    @Nonnull
    List<JobGroup> jobCategories;

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