package com.github.zighang_zighang.domain.user.controller;

import com.github.zighang_zighang.domain.user.api.UserApi;
import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.domain.user.service.UserService;
import com.github.zighang_zighang.global.auth.service.CustomUserDetails;
import com.github.zighang_zighang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @PostMapping("/filter")
    public ApiResponse<UserResponse> addOnboarding(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid UserOnboardingRequest userOnboardingRequest
            ) {
        return ApiResponse.ok(userService.addOnboarding(user.getUser(), userOnboardingRequest));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getUser(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ApiResponse.ok(userService.getMyProfile(user.getUser()));
    }

}
