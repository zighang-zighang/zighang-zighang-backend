package com.github.zighang_zighang.domain.memo.api;

import com.github.zighang_zighang.domain.memo.dto.request.UpsertMemoRequest;
import com.github.zighang_zighang.domain.memo.dto.response.AllMemosResponse;
import com.github.zighang_zighang.domain.memo.dto.response.MemoResponse;
import com.github.zighang_zighang.domain.memo.dto.response.MemosResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(
        name = "[메모]",
        description = "메모 API"
)
public interface MemoApi {

    @Operation(
            summary = "전체 메모 목록 조회 (공고별 그룹)",
            description = "사용자의 전체 메모 목록을 공고별로 그룹화하여 조회합니다."
    )
    ApiResponse<AllMemosResponse> getAllMemos(
            @Parameter(hidden = true)
            User user
    );

    @Operation(
            summary = "메모 목록 조회",
            description = "사용자의 메모 목록을 조회합니다. recruitmentId 파라미터로 특정 공고의 메모만 조회할 수 있습니다."
    )
    ApiResponse<MemosResponse> getMemos(
            @Parameter(hidden = true)
            User user,

            @Parameter(description = "공고 ID")
            UUID recruitmentId
    );

    @Operation(
            summary = "메모 생성",
            description = "특정 공고에 대한 메모를 생성합니다."
    )
    ApiResponse<MemoResponse> createMemo(
            @Parameter(hidden = true)
            User user,

            @Parameter(description = "공고 ID", required = true) UUID recruitmentId,
            UpsertMemoRequest request
    );

    @Operation(
            summary = "메모 수정",
            description = "메모를 수정합니다."
    )
    ApiResponse<MemoResponse> updateMemo(
            @Parameter(hidden = true)
            User user,

            @Parameter(description = "메모 ID", required = true) UUID memoId,
            UpsertMemoRequest request
    );

    @Operation(
            summary = "메모 삭제",
            description = "메모를 삭제합니다."
    )
    ApiResponse<Void> deleteMemo(
            @Parameter(hidden = true)
            User user,

            @Parameter(description = "메모 ID", required = true)
            UUID memoId
    );
}