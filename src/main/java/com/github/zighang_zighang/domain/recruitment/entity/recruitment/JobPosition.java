package com.github.zighang_zighang.domain.recruitment.entity.recruitment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.zighang_zighang.domain.recruitment.constant.Job;
import com.github.zighang_zighang.domain.recruitment.constant.JobGroup;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosition {

    @Nonnull
    @JsonProperty("title")
    String title;

    @Nonnull
    @JsonProperty("job")
    Job job;

    @Nonnull
    @JsonProperty("job_group")
    JobGroup jobGroup;

    @Nullable
    @JsonProperty("job_description")
    String jobDescription;
}