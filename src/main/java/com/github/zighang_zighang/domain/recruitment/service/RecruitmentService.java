package com.github.zighang_zighang.domain.recruitment.service;

import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.github.zighang_zighang.domain.recruitment.exception.RecruitmentExceptions.RECRUITMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    public RecruitmentResponse getRecruitment(UUID id) {

        Recruitment recruitment = recruitmentRepository.findById(id).orElseThrow(RECRUITMENT_NOT_FOUND::toException);

        return RecruitmentResponse.from(recruitment);
    }

    public Page<RecruitmentResponse> getRecruitments(
            List<Job> jobs,
            List<JobGroup> jobGroups,
            List<EmploymentType> employmentTypes,
            List<Education> educations,
            Integer minExperience,
            Integer maxExperience,
            List<Location> locations,
            List<EndType> endTypes,
            Integer page,
            Integer size
    ) {

        Page<Recruitment> recruitments = recruitmentRepository.findByFilters(
                jobs, jobGroups, employmentTypes, educations,
                minExperience, maxExperience, locations, endTypes, page, size
        );

        return recruitments.map(RecruitmentResponse::from);
    }
}
