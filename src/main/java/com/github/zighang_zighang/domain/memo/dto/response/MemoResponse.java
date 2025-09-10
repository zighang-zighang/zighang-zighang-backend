package com.github.zighang_zighang.domain.memo.dto.response;

import com.github.zighang_zighang.domain.memo.entity.Memo;
import com.github.zighang_zighang.domain.recruitment.constant.*;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.service.RecruitmentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoResponse {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private WrappedRecruitmentResponse recruitment;
    private String title;
    private String content;

    public static MemoResponse from(Memo memo, RecruitmentService service) {

        RecruitmentResponse recruitment = service.getRecruitment(memo.getRecruitmentId());

        return MemoResponse.builder()
                .id(memo.getId())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .recruitment(WrappedRecruitmentResponse.from(recruitment))
                .title(memo.getTitle())
                .content(memo.getContent())
                .build();
    }

    public record WrappedRecruitmentResponse(
            UUID id,
            String title,
            String imageUrl,
            List<Location> locations,
            Integer minExperience,
            Integer maxExperience,
            List<Education> educations,
            LocalDateTime startDate,
            LocalDateTime endDate,
            DeadlineType deadlineType,
            List<EmploymentType> employmentTypes,
            List<Job> jobs,
            List<JobCategory> jobCategories
    ) {

        public static WrappedRecruitmentResponse from(RecruitmentResponse response) {
            return new WrappedRecruitmentResponse(
                    response.getId(),
                    response.getTitle(),
                    response.getImageUrl(),
                    response.getLocations(),
                    response.getMinExperience(),
                    response.getMaxExperience(),
                    response.getEducations(),
                    response.getStartDate(),
                    response.getEndDate(),
                    response.getDeadlineType(),
                    response.getEmploymentTypes(),
                    response.getJobs(),
                    response.getJobCategories()
            );
        }
    }
}