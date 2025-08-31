package com.github.zighang_zighang.domain.recruitment.repository;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.global.response.PageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecruitmentRepository {

    Optional<Recruitment> findById(UUID id);

    PageResponse<Recruitment> findByFilters(
            List<Job> jobs,
            List<JobCategory> jobCategories,
            List<EmploymentType> employmentTypes,
            List<Education> educations,
            Integer minExperience,
            Integer maxExperience,
            List<Location> locations,
            List<DeadlineType> deadlineTypes,
            Integer page,
            Integer size
    );
}
