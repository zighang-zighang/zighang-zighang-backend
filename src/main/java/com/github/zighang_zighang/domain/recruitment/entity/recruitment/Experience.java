package com.github.zighang_zighang.domain.recruitment.entity.recruitment;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("min_experience")
    Integer minExperience;

    @Nullable
    @JsonProperty("max_experience")
    Integer maxExperience;
}