package com.github.zighang_zighang.domain.memo.dto.response;

import com.github.zighang_zighang.domain.memo.entity.Memo;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.service.RecruitmentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoResponse {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private RecruitmentResponse recruitment;
    private String title;
    private String content;

    public static MemoResponse from(Memo memo, RecruitmentService service) {

        return MemoResponse.builder()
                .id(memo.getId())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .recruitment(service.getRecruitment(null, memo.getRecruitmentId()))
                .title(memo.getTitle())
                .content(memo.getContent())
                .build();
    }
}