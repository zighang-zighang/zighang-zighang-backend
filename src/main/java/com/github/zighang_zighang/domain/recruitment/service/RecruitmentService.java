package com.github.zighang_zighang.domain.recruitment.service;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.github.zighang_zighang.domain.recruitment.exception.RecruitmentExceptions.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    @Cacheable(value = "recruitment", key = "#id")
    public RecruitmentResponse getRecruitment(UUID id) {

        Recruitment recruitment = recruitmentRepository.findById(id).orElseThrow(NOT_FOUND::toException);

        return RecruitmentResponse.from(recruitment);
    }

    @Cacheable(
            value = "recruitments",
            key = "T(java.util.Objects).hash(#jobs, #jobCategories, #employmentTypes, #educations," +
                    "#minExperience, #maxExperience, #locations, #deadlineTypes, #page, #size)"
    )
    public PageResponse<RecruitmentResponse> getRecruitments(
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
    ) {

        PageResponse<Recruitment> recruitments = recruitmentRepository.findByFilters(
                jobs, jobCategories, employmentTypes, educations,
                minExperience, maxExperience, locations, deadlineTypes, page, size
        );

        return recruitments.map(RecruitmentResponse::from);
    }
}
