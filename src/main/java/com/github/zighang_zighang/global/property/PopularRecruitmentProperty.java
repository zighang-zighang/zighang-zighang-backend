package com.github.zighang_zighang.global.property;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "popular.recruitment")
public class PopularRecruitmentProperty {

    @NotNull
    @Min(0)
    @Max(1)
    Double viewWeight;

    @NotNull
    @Min(0)
    @Max(1)
    Double bookmarkWeight;

    @NotNull
    @Min(0)
    @Max(1)
    Double applicationWeight;

    @NotNull
    @Min(1)
    Integer viewHours;

    @NotNull
    @Min(1)
    Integer bookmarkHours;

    @NotNull
    @Min(1)
    Integer applicationHours;
}