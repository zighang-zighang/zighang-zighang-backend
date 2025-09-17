package com.github.zighang_zighang.domain.recruitment.service;

import com.github.zighang_zighang.domain.bookmark.repository.BookmarkRepository;
import com.github.zighang_zighang.domain.recruitment.dto.request.RecruitmentSearchRequest;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentApplication;
import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentApplicationRepository;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentViewRepository;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.property.PopularRecruitmentProperty;
import com.github.zighang_zighang.global.response.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.zighang_zighang.domain.recruitment.exception.RecruitmentExceptions.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentViewRepository recruitmentViewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final RecruitmentApplicationRepository recruitmentApplicationRepository;
    private final PopularRecruitmentProperty popularRecruitmentProperty;

    @Transactional
    public RecruitmentResponse getRecruitment(UUID id) {

        Recruitment recruitment = recruitmentRepository.findById(id).orElseThrow(NOT_FOUND::toException);

        return RecruitmentResponse.from(recruitment, 0, false);
    }

    @Transactional
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

        int views = recruitmentViewRepository.countByRecruitmentId(id);
        boolean bookmarked = Objects.nonNull(user) && bookmarkRepository.existsByUserAndRecruitmentId(user, id);

        return RecruitmentResponse.from(recruitment, views, bookmarked);
    }

    public PageResponse<RecruitmentResponse> getRecruitments(
            User user,
            RecruitmentSearchRequest request
    ) {

        PageResponse<Recruitment> recruitments = recruitmentRepository.findByFilters(
                request.getJobs(), request.getJobCategories(), request.getEmploymentTypes(), request.getEducations(),
                request.getMinExperience(), request.getMaxExperience(), request.getLocations(), request.getDeadlineTypes(), request.getPage(), request.getSize()
        );

        Set<UUID> ids = recruitments.getContent().stream().map(Recruitment::getId).collect(Collectors.toSet());

        Set<UUID> bookmarked = Optional.ofNullable(user)
                .map((u) -> bookmarkRepository.findBookmarkedRecruitmentIds(u, ids))
                .orElseGet(Collections::emptySet);

        Map<UUID, Long> count = recruitmentViewRepository.findAll().stream()
                .filter(rv -> ids.contains(rv.getRecruitmentId()))
                .collect(Collectors.groupingBy(RecruitmentView::getRecruitmentId, Collectors.counting()));

        return recruitments.map(r -> RecruitmentResponse.from(r, count.getOrDefault(r.getId(), 0L).intValue(), bookmarked.contains(r.getId())));
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

    public List<RecruitmentResponse> getPopularRecruitments(User user, List<Job> jobs) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime viewCutoff = now.minusHours(popularRecruitmentProperty.getViewHours());
        LocalDateTime bookmarkCutoff = now.minusHours(popularRecruitmentProperty.getBookmarkHours());
        LocalDateTime applicationCutoff = now.minusHours(popularRecruitmentProperty.getApplicationHours());

        List<Recruitment> recruitments = recruitmentRepository.findPopularRecruitmentIds(
                jobs,
                viewCutoff, bookmarkCutoff, applicationCutoff,
                popularRecruitmentProperty.getViewWeight(),
                popularRecruitmentProperty.getBookmarkWeight(),
                popularRecruitmentProperty.getApplicationWeight()
        );

        Set<UUID> ids = recruitments.stream().map(Recruitment::getId).collect(Collectors.toSet());

        Set<UUID> bookmarked = Optional.ofNullable(user)
                .map((u) -> bookmarkRepository.findBookmarkedRecruitmentIds(u, ids))
                .orElseGet(Collections::emptySet);

        Map<UUID, Long> count = recruitmentViewRepository.findAll().stream()
                .filter(rv -> ids.contains(rv.getRecruitmentId()))
                .collect(Collectors.groupingBy(RecruitmentView::getRecruitmentId, Collectors.counting()));

        return recruitments.stream()
                .filter(Objects::nonNull)
                .map(recruitment -> RecruitmentResponse.from(recruitment, count.getOrDefault(recruitment.getId(), 0L).intValue(), bookmarked.contains(recruitment.getId())))
                .toList();
    }
}
