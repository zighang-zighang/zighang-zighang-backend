package com.github.zighang_zighang.domain.recruitment.entity.recruitment;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Experience {

    @Nullable
    Integer minExperience;

    @Nullable
    Integer maxExperience;
}