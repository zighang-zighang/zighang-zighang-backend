package com.github.zighang_zighang.domain.resume.dto.response;

import com.github.zighang_zighang.domain.resume.entity.Resume;
import com.github.zighang_zighang.global.common.annotation.DateFormatDot;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Schema
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ResumeResponse {

    @Schema
    private UUID id;

    @Schema(description = "파일명")
    private String fileName;

    @Schema(description = "파일 url")
    private String fileUrl;

    @Schema(description = "파일 사이즈 (byte 단위)")
    private long size;

    @DateFormatDot
    @Schema(description = "업로드일")
    private LocalDate uploadDate;

    public static ResumeResponse from(Resume resume) {

        return ResumeResponse.of(
                resume.getId(),
                resume.getName(),
                resume.getUrl(),
                resume.getSize(),
                resume.getCreatedAt().toLocalDate()
        );

    }
}
