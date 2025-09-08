package com.github.zighang_zighang.domain.user.controller;

import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.domain.user.service.UserService;
import com.github.zighang_zighang.global.auth.service.CustomUserDetails;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/filter")
    public ApiResponse<UserResponse> addOnboarding(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody UserOnboardingRequest userOnboardingRequest
            ) {
        return ApiResponse.ok(userService.addOnboarding(user.getUser(), userOnboardingRequest));
    }

}
