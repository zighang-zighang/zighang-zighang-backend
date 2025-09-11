package com.github.zighang_zighang.domain.recruitment.dto.request;

import com.github.zighang_zighang.domain.recruitment.constant.DeadlineType;
import com.github.zighang_zighang.domain.recruitment.constant.Education;
import com.github.zighang_zighang.domain.recruitment.constant.EmploymentType;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.classification.Location;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
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