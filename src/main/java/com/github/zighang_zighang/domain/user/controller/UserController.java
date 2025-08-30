package com.github.zighang_zighang.domain.user.controller;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.service.UserService;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> getUserProfile(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ApiResponse.error("AUTH_REQUIRED", "로그인이 필요합니다");
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");

        Map<String, Object> profile = Map.of(
            "email", email,
            "name", name,
            "picture", picture
        );

        return ApiResponse.ok(profile);
    }
}
