package com.github.zighang_zighang.domain.user.controller;

import com.github.zighang_zighang.domain.user.api.UserApi;
import com.github.zighang_zighang.domain.user.dto.request.UserOnboardingRequest;
import com.github.zighang_zighang.domain.user.dto.response.UserResponse;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.service.UserService;
import com.github.zighang_zighang.global.auth.resolver.CurrentUser;
import com.github.zighang_zighang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @PostMapping("/filter")
    public ApiResponse<UserResponse> addOnboarding(
            @CurrentUser User user,
            @RequestBody @Valid UserOnboardingRequest userOnboardingRequest
            ) {
        return ApiResponse.ok(userService.addOnboarding(user, userOnboardingRequest));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getUser(
            @CurrentUser User user
    ) {
        return ApiResponse.ok(userService.getMyProfile(user));
    }

}
