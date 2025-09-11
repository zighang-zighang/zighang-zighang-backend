package com.github.zighang_zighang.domain.resume.api;

import com.github.zighang_zighang.domain.resume.dto.response.ResumeResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "[이력서]",
        description = "이력서/자기소개서 관리 API"
)
public interface ResumeApi {

    @Operation(
            summary = "이력서 업로드",
            description = "새로운 이력서를 업로드하고 텍스트를 추출합니다. (PDF/HWP 지원)"
    )
    ApiResponse<ResumeResponse> uploadResume(
            @Parameter(hidden = true) User user,

            @Parameter(description = "업로드할 이력서 파일 (PDF/HWP)")
            MultipartFile resumeFile
    );

    @Operation(
            summary = "이력서 목록 조회",
            description = "현재 로그인한 사용자의 모든 이력서를 조회합니다."
    )
    ApiResponse<List<ResumeResponse>> getAllResumes(
            @Parameter(hidden = true) User user
    );

    @Operation(
            summary = "이력서 삭제",
            description = "이력서를 삭제합니다. (본인 소유만 가능)"
    )
    ApiResponse<Void> deleteResume(
            @Parameter(hidden = true) User user,

            @Parameter(description = "삭제할 이력서 ID")
            UUID resumeId
    );
}
