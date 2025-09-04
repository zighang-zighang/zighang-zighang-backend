package com.github.zighang_zighang.domain.bookmark.controller;

import com.github.zighang_zighang.domain.bookmark.api.BookmarkApi;
import com.github.zighang_zighang.domain.bookmark.service.BookmarkService;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TODO: 시큐리티 구성 후 실제 유저 주입
// TODO: 시큐리티 구성 후 컨트롤러 가드 걸기
@Validated
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkApi {

    private final BookmarkService bookmarkService;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<RecruitmentResponse>> getBookmarks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {

        return ApiResponse.ok(bookmarkService.getBookmarks(null, page, size));
    }

    @Override
    @PostMapping("/{recruitmentId}")
    public ApiResponse<Void> addBookmark(
            @PathVariable UUID recruitmentId
    ) {

        bookmarkService.addBookmark(null, recruitmentId);

        return ApiResponse.ok();
    }

    @Override
    @DeleteMapping("/{recruitmentId}")
    public ApiResponse<Void> removeBookmark(
            @PathVariable UUID recruitmentId
    ) {

        bookmarkService.removeBookmark(null, recruitmentId);

        return ApiResponse.ok();
    }
}
