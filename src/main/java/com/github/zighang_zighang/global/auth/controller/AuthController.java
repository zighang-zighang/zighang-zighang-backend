package com.github.zighang_zighang.global.auth.controller;

import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.service.AuthService;
import com.github.zighang_zighang.global.auth.service.TokenStorageService;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenStorageService tokenStorageService;

    @PostMapping("/auth/refresh")
    public ApiResponse<LoginResponse> refreshToken(@RequestParam String userId) {
        // Redis에서 userId로 refresh token 조회
        String refreshToken = tokenStorageService.getRefreshToken(userId);
        if (refreshToken == null) {
            return ApiResponse.error("REFRESH_TOKEN_NOT_FOUND", "Refresh token을 찾을 수 없습니다.");
        }
        
        LoginResponse response = authService.refreshToken(refreshToken);
        return ApiResponse.ok(response);
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, 
                       @RequestParam(value = "details", required = false) String details) {
        StringBuilder html = new StringBuilder();
        html.append("<h1>로그인 페이지</h1>");
        
        if (error != null) {
            html.append("<div style='background: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin: 10px 0;'>");
            html.append("<strong>로그인 오류:</strong> ").append(error);
            if (details != null) {
                html.append("<br><strong>오류 상세:</strong> ").append(details);
            }
            html.append("</div>");
        }
        
        html.append("<p>소셜 로그인으로 로그인하세요</p>");
        html.append("<p><a href=\"/oauth2/authorization/google\" style=\"background: #4285f4; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 5px;\">Google로 로그인</a></p>");
        html.append("<p><a href=\"/oauth2/authorization/kakao\" style=\"background: #FEE500; color: #000; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 5px;\">카카오로 로그인</a></p>");
        html.append("<p><a href=\"/oauth2/authorization/naver\" style=\"background: #03C75A; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 5px;\">네이버로 로그인</a></p>");
        
        return html.toString();
    }
}
