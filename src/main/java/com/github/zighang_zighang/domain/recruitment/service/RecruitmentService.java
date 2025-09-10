package com.github.zighang_zighang.domain.recruitment.service;

import com.github.zighang_zighang.domain.bookmark.repository.BookmarkRepository;
import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.domain.recruitment.entity.RecruitmentView;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentRepository;
import com.github.zighang_zighang.domain.recruitment.repository.RecruitmentViewRepository;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.github.zighang_zighang.domain.recruitment.exception.RecruitmentExceptions.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentViewRepository recruitmentViewRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public RecruitmentResponse getRecruitment(User user, UUID id) {

        Recruitment recruitment = recruitmentRepository.findById(id).orElseThrow(NOT_FOUND::toException);

        if (Objects.nonNull(user)) {

            RecruitmentView view = recruitmentViewRepository.findByUserAndRecruitmentId(user, id)
                    .orElseGet(() ->
                            recruitmentViewRepository.save(
                                    RecruitmentView.builder()
                                            .user(user)
                                            .recruitmentId(id)
                                            .viewCount(0)
                                            .build()
                            )
                    );

            view.addViewCount();
        }

        Boolean isBookmarked = Objects.nonNull(user) && bookmarkRepository.existsByUserAndRecruitmentId(user, id);

        return RecruitmentResponse.from(recruitment, isBookmarked);
    }

    @Cacheable(
            value = "recruitments",
            key = "T(java.util.Objects).hash(#jobs, #jobCategories, #employmentTypes, #educations," +
                    "#minExperience, #maxExperience, #locations, #deadlineTypes, #page, #size)"
    )
    public PageResponse<RecruitmentResponse> getRecruitments(
            User user,
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

        List<UUID> ids = recruitments.getContent().stream().map(Recruitment::getId).toList();
        Set<UUID> bookmarked = bookmarkRepository.findBookmarkedRecruitmentIds(user, ids);

        return recruitments.map(r -> RecruitmentResponse.from(r, bookmarked.contains(r.getId())));
    }
}
