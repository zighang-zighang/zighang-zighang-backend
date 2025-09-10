package com.github.zighang_zighang.domain.recruitment.service;

import com.github.zighang_zighang.domain.bookmark.repository.BookmarkRepository;
import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.request.RecruitmentSearchRequest;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentApplication;
import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentApplicationRepository;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentViewRepository;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.github.zighang_zighang.domain.recruitment.exception.RecruitmentExceptions.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentViewRepository recruitmentViewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RecruitmentApplicationRepository recruitmentApplicationRepository;

    @Transactional
    @Cacheable(value = "recruitment", key = "#id", sync = true)
    public RecruitmentResponse getRecruitment(UUID id) {

        Recruitment recruitment = recruitmentRepository.findById(id).orElseThrow(NOT_FOUND::toException);

        return RecruitmentResponse.from(recruitment);
    }

    @Transactional
    @Cacheable(
            value = "recruitment-view",
            key = "#id + ':' + #ipAddress + ':' + T(java.lang.String).valueOf(#userAgent)"
    )
    public RecruitmentResponse getRecruitment(User user, UUID id, String ipAddress, String userAgent) {

        Recruitment recruitment = recruitmentRepository.findById(id).orElseThrow(NOT_FOUND::toException);

        recruitmentViewRepository.save(
                RecruitmentView.builder()
                        .recruitmentId(id)
                        .user(user)
                        .ipAddress(ipAddress)
                        .userAgent(Objects.isNull(userAgent) ? "Unknown" : userAgent.length() > 1000 ? userAgent.substring(0, 1000) : userAgent)
                        .build()
        );

        Boolean isBookmarked = Objects.nonNull(user) && bookmarkRepository.existsByUserAndRecruitmentId(user, id);

        return RecruitmentResponse.from(recruitment, isBookmarked);
    }

    @Cacheable(
            value = "recruitments",
            key = "T(java.util.Objects).hash(#request.jobs, #request.jobCategories, #request.employmentTypes, #request.educations," +
                    "#request.minExperience, #request.maxExperience, #request.locations, #request.deadlineTypes, #request.page, #request.size)"
    )
    public PageResponse<RecruitmentResponse> getRecruitments(
            User user,
            RecruitmentSearchRequest request
    ) {

        PageResponse<Recruitment> recruitments = recruitmentRepository.findByFilters(
                request.getJobs(), request.getJobCategories(), request.getEmploymentTypes(), request.getEducations(),
                request.getMinExperience(), request.getMaxExperience(), request.getLocations(), request.getDeadlineTypes(), request.getPage(), request.getSize()
        );

        Set<UUID> bookmarked = Optional.ofNullable(user)
                .map((u) -> {
                    List<UUID> ids = recruitments.getContent().stream().map(Recruitment::getId).toList();
                    return bookmarkRepository.findBookmarkedRecruitmentIds(u, ids);
                })
                .orElseGet(Collections::emptySet);

        return recruitments.map(r -> RecruitmentResponse.from(r, bookmarked.contains(r.getId())));
    }

    @Transactional
    public void logApplication(User user, UUID recruitmentId) {

        try {
            recruitmentApplicationRepository.save(
                    RecruitmentApplication.builder()
                            .user(user)
                            .recruitmentId(recruitmentId)
                            .build()
            );
        } catch (DataIntegrityViolationException ignored) {
        }
    }
}
