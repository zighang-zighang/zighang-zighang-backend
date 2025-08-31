package com.github.zighang_zighang.domain.recruitment.controller;

import com.github.zighang_zighang.domain.recruitment.api.RecruitmentApi;
import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.response.PageResponse;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.service.RecruitmentService;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recruitments")
@RequiredArgsConstructor
public class RecruitmentController implements RecruitmentApi {

    private final RecruitmentService recruitmentService;

    @Override
    @GetMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentResponse> getRecruitment(@PathVariable UUID recruitmentId) {

        return ApiResponse.ok(recruitmentService.getRecruitment(recruitmentId));
    }

    @Override
    @GetMapping
    public ApiResponse<PageResponse<RecruitmentResponse>> getRecruitments(
            @RequestParam(required = false) List<Job> jobs,
            @RequestParam(required = false) List<JobCategory> jobCategories,
            @RequestParam(required = false) List<EmploymentType> employmentTypes,
            @RequestParam(required = false) List<Education> educations,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) List<Location> locations,
            @RequestParam(required = false) List<DeadlineType> deadlineTypes,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {

        return ApiResponse.ok(PageResponse.from(recruitmentService.getRecruitments(
                jobs, jobCategories, employmentTypes, educations,
                minExperience, maxExperience, locations, deadlineTypes, page, size
        )));
    }
}
