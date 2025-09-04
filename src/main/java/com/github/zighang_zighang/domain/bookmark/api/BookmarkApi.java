package com.github.zighang_zighang.domain.bookmark.api;

import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

@Tag(
        name = "[북마크]",
        description = "북마크 API"
)
public interface BookmarkApi {

    @Operation(
            summary = "북마크한 공고 목록 조회",
            description = "북마크한 공고 목록을 조회합니다. 페이지네이션이 적용됩니다."
    )
    ApiResponse<PageResponse<RecruitmentResponse>> getBookmarks(
            @Min(0)
            @Parameter(description = "페이지")
            Integer page,

            @Min(1)
            @Max(100)
            @Parameter(description = "페이지 크기")
            Integer size
    );

    @Operation(
            summary = "북마크 추가",
            description = "북마크를 추가합니다."
    )
    ApiResponse<Void> addBookmark(
            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );

    @Operation(
            summary = "북마크 삭제",
            description = "북마크를 삭제합니다."
    )
    ApiResponse<Void> removeBookmark(
            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );
}
