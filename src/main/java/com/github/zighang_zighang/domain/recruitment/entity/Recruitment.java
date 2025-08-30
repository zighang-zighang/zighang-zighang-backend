package com.github.zighang_zighang.domain.recruitment.entity;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.entity.recruitment.Experience;
import com.github.zighang_zighang.domain.recruitment.entity.recruitment.JobPosition;
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
    String companyName;

    @Nonnull
    String title;

    @Nonnull
    List<JobPosition> jobPositions;

    @Nullable
    LocalDateTime recruitmentStart;

    @Nullable
    LocalDateTime recruitmentEnd;

    @Nonnull
    EndType endType;

    @Nullable
    Location location;

    @Nonnull
    Education education;

    @Nonnull
    Experience experience;

    @Nullable
    EmploymentType employmentType;

    @Nullable
    CompanySize companySize;

    @Nullable
    String organizationIntroduction;

    @Nonnull
    List<String> qualificationRequirements;

    @Nonnull
    List<String> preferences;

    @Nonnull
    List<String> benefits;

    @Nullable
    String salary;

    @Nonnull
    String recruitmentUrl;
}