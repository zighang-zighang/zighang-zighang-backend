package com.github.zighang_zighang.domain.recruitment.dto.response;

import com.github.zighang_zighang.domain.recruitment.constant.DeadlineType;
import com.github.zighang_zighang.domain.recruitment.entity.Recruitment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class SimpleRecruitmentResponse {

    @Schema(description = "공고 ID")
    UUID id;

    @Schema(description = "채용 공고 제목")
    String title;

    @Schema(description = "채용 마감일")
    LocalDateTime endDate;

    @Schema(description = "마감 타입")
    DeadlineType deadlineType;

    @Schema(description = "회사 이름")
    String companyName;

    public static SimpleRecruitmentResponse from(Recruitment recruitment) {

        return SimpleRecruitmentResponse.of(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getEndDate(),
                recruitment.getDeadlineType(),
                recruitment.getCompanyName()
        );
    }

    public static SimpleRecruitmentResponse from(RecruitmentResponse recruitmentResponse) {

        return SimpleRecruitmentResponse.of(
                recruitmentResponse.getId(),
                recruitmentResponse.getTitle(),
                recruitmentResponse.getEndDate(),
                recruitmentResponse.getDeadlineType(),
                recruitmentResponse.getCompanyName()
        );
    }
}
