package com.github.zighang_zighang.domain.recruitment.controller;

import com.github.zighang_zighang.domain.recruitment.api.RecruitmentApi;
import com.github.zighang_zighang.domain.recruitment.dto.request.RecruitmentSearchRequest;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.service.RecruitmentService;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.auth.resolver.CurrentUser;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.global.response.PageResponse;
import com.github.zighang_zighang.global.util.RequestParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/recruitments")
@RequiredArgsConstructor
public class RecruitmentController implements RecruitmentApi {

    private final RecruitmentService recruitmentService;

    @Override
    @GetMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentResponse> getRecruitment(
            HttpServletRequest request,
            @CurrentUser User user,
            @PathVariable UUID recruitmentId
    ) {
        String ipAddress = RequestParser.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        return ApiResponse.ok(recruitmentService.getRecruitment(user, recruitmentId, ipAddress, userAgent));
    }

    @Override
    @GetMapping
    public ApiResponse<PageResponse<RecruitmentResponse>> getRecruitments(
            @CurrentUser User user,
            @ModelAttribute RecruitmentSearchRequest request
    ) {

        return ApiResponse.ok(recruitmentService.getRecruitments(user, request));
    }

    @Override
    @PostMapping("/{recruitmentId}/applications/log")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> logApplication(
            @CurrentUser User user,
            @PathVariable UUID recruitmentId
    ) {

        recruitmentService.logApplication(user, recruitmentId);

        return ApiResponse.ok();
    }
}
