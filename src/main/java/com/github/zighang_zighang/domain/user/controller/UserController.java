package com.github.zighang_zighang.domain.user.controller;

import com.github.zighang_zighang.domain.recruitment.dto.response.RecommendedRecruitmentResponse;
import com.github.zighang_zighang.domain.recruitment.dto.response.RecruitmentResponse;
import com.github.zighang_zighang.domain.user.api.UserApi;
import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.service.UserService;
import com.github.zighang_zighang.global.auth.resolver.CurrentUser;
import com.github.zighang_zighang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @PostMapping(value = "/filter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> addOnboarding(
            @CurrentUser User user,
            @RequestPart("request") @Valid UserOnboardingRequest userOnboardingRequest,
            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile
    ) {
        return ApiResponse.ok(userService.addOnboarding(user, userOnboardingRequest, resumeFile));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getUser(
            @CurrentUser User user
    ) {
        return ApiResponse.ok(userService.getMyProfile(user));
    }

    // TODO: User 도메인이 아닌 더 적합한 도메인으로 컨트롤러 변경
    @GetMapping("/recommend")
    public ApiResponse<List<RecommendedRecruitmentResponse>> recommendJobs(
            @CurrentUser User user
    ) {
       return ApiResponse.ok(userService.recommendJobs(user));
    }

}
