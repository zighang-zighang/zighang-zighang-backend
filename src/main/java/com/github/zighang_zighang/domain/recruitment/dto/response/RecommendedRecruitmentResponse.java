package com.github.zighang_zighang.domain.recruitment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.zighang_zighang.domain.recruitment.constant.Education;
import com.github.zighang_zighang.domain.recruitment.constant.EmploymentType;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import com.github.zighang_zighang.global.classification.Job;
import com.github.zighang_zighang.global.classification.JobCategory;
import com.github.zighang_zighang.global.classification.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // vector나 기타 불필요 필드는 무시 가능하게 해 줌
public class RecommendedRecruitmentResponse {

    private UUID id;
    private String title;
    private String description; // RecruitmentResponse에는 없었지만 응답에는 있음
    private String recruitmentUrl;
    private String imageUrl;

    private List<Location> locations;
    private Integer minExperience;
    private Integer maxExperience;
    private List<Education> educations;

//     TODO: OffsetDateTime(ex.1750047240.000000000) -> LocalDateTime 형식으로 수정
//    private OffsetDateTime startDate;
//    private OffsetDateTime endDate;

//    private String deadlineType;
    private List<EmploymentType> employmentTypes;
    private List<Job> jobs;
    private List<JobCategory> jobCategories;

    private String companyName;
    private String companyDescription;
    private String companyImageUrl;
//    private String companySize;

    // TODO: 피그마 보고 키워드 등 추가 필요 필드 추가
    @Schema(description = "북마크 여부")
    private Boolean isBookmarked;

    public static RecommendedRecruitmentResponse from(Recruitment recruitment, boolean bookmarked) {
        return RecommendedRecruitmentResponse.of(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getDescription(),
                recruitment.getRecruitmentUrl(),
                recruitment.getImageUrl(),
                recruitment.getLocations(),
                recruitment.getMaxExperience(),
                recruitment.getMaxExperience(),
                recruitment.getEducations(),
                recruitment.getEmploymentTypes(),
                recruitment.getJobs(),
                recruitment.getJobCategories(),
                recruitment.getCompanyName(),
                recruitment.getCompanyDescription(),
                recruitment.getCompanyImageUrl(),
                bookmarked
        );
    }

}

