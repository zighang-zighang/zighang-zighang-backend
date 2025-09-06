package com.github.zighang_zighang.domain.bookmark.controller;

import com.github.zighang_zighang.domain.bookmark.api.BookmarkApi;
import com.github.zighang_zighang.domain.bookmark.service.BookmarkService;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.global.auth.service.CustomUserDetails;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkApi {

    private final BookmarkService bookmarkService;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<RecruitmentResponse>> getBookmarks(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {

        return ApiResponse.ok(bookmarkService.getBookmarks(user.getUser(), page, size));
    }

    @Override
    @PostMapping("/{recruitmentId}")
    public ApiResponse<Void> addBookmark(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable UUID recruitmentId
    ) {

        bookmarkService.addBookmark(user.getUser(), recruitmentId);

        return ApiResponse.ok();
    }

    @Override
    @DeleteMapping("/{recruitmentId}")
    public ApiResponse<Void> removeBookmark(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable UUID recruitmentId
    ) {

        bookmarkService.removeBookmark(user.getUser(), recruitmentId);

        return ApiResponse.ok();
    }
}
