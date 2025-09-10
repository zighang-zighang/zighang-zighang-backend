package com.github.zighang_zighang.domain.recruitment.dto.request;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.global.classification.Job;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class RecruitmentSearchRequest {

    @Parameter(in = ParameterIn.QUERY)
    List<Job> jobs;

    @Parameter(in = ParameterIn.QUERY)
    List<JobCategory> jobCategories;

    @Parameter(in = ParameterIn.QUERY)
    List<EmploymentType> employmentTypes;

    @Parameter(in = ParameterIn.QUERY)
    List<Education> educations;

    @Parameter(in = ParameterIn.QUERY)
    Integer minExperience;

    @Parameter(in = ParameterIn.QUERY)
    Integer maxExperience;

    @Parameter(in = ParameterIn.QUERY)
    List<Location> locations;

    @Parameter(in = ParameterIn.QUERY)
    List<DeadlineType> deadlineTypes;

    @Parameter(in = ParameterIn.QUERY)
    Integer page = 0;

    @Parameter(in = ParameterIn.QUERY)
    Integer size = 20;
}