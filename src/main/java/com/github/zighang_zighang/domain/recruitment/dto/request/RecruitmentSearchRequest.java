package com.github.zighang_zighang.domain.recruitment.dto.request;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@Schema
@Getter
@Setter
@ParameterObject
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentSearchRequest {

    @Parameter
    List<Job> jobs;

    @Parameter
    List<JobCategory> jobCategories;

    @Parameter
    List<EmploymentType> employmentTypes;

    @Parameter
    List<Education> educations;

    @Parameter
    Integer minExperience;

    @Parameter
    Integer maxExperience;

    @Parameter
    List<Location> locations;

    @Parameter
    List<DeadlineType> deadlineTypes;

    @Parameter
    Integer page = 0;

    @Parameter
    Integer size = 20;
}