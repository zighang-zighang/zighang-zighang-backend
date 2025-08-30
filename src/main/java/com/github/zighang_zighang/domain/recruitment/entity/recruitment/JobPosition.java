package com.github.zighang_zighang.domain.recruitment.entity.recruitment;

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
    String title;

    @Nonnull
    Job job;

    @Nonnull
    JobGroup jobGroup;

    @Nullable
    String jobDescription;
}