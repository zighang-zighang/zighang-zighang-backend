package com.github.zighang_zighang.domain.memo.controller;

import com.github.zighang_zighang.domain.memo.api.MemoApi;
import com.github.zighang_zighang.domain.memo.dto.request.UpsertMemoRequest;
import com.github.zighang_zighang.domain.memo.dto.response.MemoResponse;
import com.github.zighang_zighang.domain.memo.dto.response.MemosResponse;
import com.github.zighang_zighang.domain.memo.service.MemoService;
import com.github.zighang_zighang.global.auth.service.CustomUserDetails;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/memos")
@RequiredArgsConstructor
public class MemoController implements MemoApi {

    private final MemoService memoService;

    @Override
    @GetMapping
    public ApiResponse<MemosResponse> getMemos(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) UUID recruitmentId
    ) {

        return ApiResponse.ok(memoService.getMemos(user.getUser(), recruitmentId));
    }

    @Override
    @PostMapping
    public ApiResponse<MemoResponse> createMemo(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam UUID recruitmentId,
            @RequestBody @Validated UpsertMemoRequest request
    ) {

        return ApiResponse.ok(memoService.createMemo(user.getUser(), recruitmentId, request));
    }

    @Override
    @PutMapping("/{memoId}")
    public ApiResponse<MemoResponse> updateMemo(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable UUID memoId,
            @RequestBody @Validated UpsertMemoRequest request
    ) {
        return ApiResponse.ok(memoService.updateMemo(user.getUser(), memoId, request));
    }

    @Override
    @DeleteMapping("/{memoId}")
    public ApiResponse<Void> deleteMemo(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable UUID memoId
    ) {
        memoService.deleteMemo(user.getUser(), memoId);

        return ApiResponse.ok();
    }
}