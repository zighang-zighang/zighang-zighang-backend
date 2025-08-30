package com.github.zighang_zighang.domain.recruitment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.entity.recruitment.Experience;
import com.github.zighang_zighang.domain.recruitment.entity.recruitment.JobPosition;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString(exclude = "embedding")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment {

    @Nonnull
    @JsonProperty("company_name")
    String companyName;

    @Nonnull
    @JsonProperty("title")
    String title;

    @Nonnull
    @JsonProperty("job_positions")
    List<JobPosition> jobPositions;

    @Nullable
    @JsonProperty("recruitment_start")
    LocalDateTime recruitmentStart;

    @Nullable
    @JsonProperty("recruitment_end")
    LocalDateTime recruitmentEnd;

    @Nonnull
    @JsonProperty("end_type")
    EndType endType;

    @Nullable
    @JsonProperty("location")
    Location location;

    @Nonnull
    @JsonProperty("education")
    Education education;

    @Nonnull
    @JsonProperty("experience")
    Experience experience;

    @Nullable
    @JsonProperty("employment_type")
    EmploymentType employmentType;

    @Nullable
    @JsonProperty("company_size")
    CompanySize companySize;

    @Nullable
    @JsonProperty("organization_introduction")
    String organizationIntroduction;

    @Nonnull
    @JsonProperty("qualification_requirements")
    List<String> qualificationRequirements;

    @Nonnull
    @JsonProperty("preferences")
    List<String> preferences;

    @Nonnull
    @JsonProperty("benefits")
    List<String> benefits;

    @Nullable
    @JsonProperty("salary")
    String salary;

    @Nonnull
    @JsonProperty("recruitment_url")
    String recruitmentUrl;

    @Nonnull
    @JsonProperty("embedding")
    List<Double> embedding;
}