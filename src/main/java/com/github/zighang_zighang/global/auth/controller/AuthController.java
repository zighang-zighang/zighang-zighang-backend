package com.github.zighang_zighang.global.auth.controller;

import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.dto.TokenRefreshResponse;
import com.github.zighang_zighang.global.auth.service.AuthService;
import com.github.zighang_zighang.global.auth.service.TokenStorageService;
import com.github.zighang_zighang.global.auth.util.JwtUtil;
import com.github.zighang_zighang.global.exception.ApiException;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenStorageService tokenStorageService;
    private final JwtUtil jwtUtil;

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestHeader(value = "Refresh-Token", required = false) String refreshTokenHeader) {
        
        try {
            // Refresh Token 검증
            String refreshToken = refreshTokenHeader;
            if (refreshToken == null || refreshToken.isBlank()) {
                return ResponseEntity
                    .status(401)
                    .body(ApiResponse.error(AuthExceptionCode.TOKEN_NOT_FOUND.getCode(), 
                                          "Refresh Token이 필요합니다."));
            }

            TokenRefreshResponse tokenResponse = authService.refreshToken(refreshToken);
            
            // 응답 헤더에 토큰과 캐시 방지 및 보안 헤더 설정
            return ResponseEntity
                .ok()
                .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
                .header("Refresh-Token", tokenResponse.getRefreshToken())
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(ApiResponse.ok(tokenResponse.getUserInfo()));
                
        } catch (ApiException e) {
            return ResponseEntity
                .status(401)
                .body(ApiResponse.error(e.getErrorCode(), e.getErrorMessage()));
        }
    }

        @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, 
                       @RequestParam(value = "details", required = false) String details) {
        // XSS 방지를 위해 HTML 이스케이프 처리
        String safeError = HtmlUtils.htmlEscape(error == null ? "" : error);
        String safeDetails = HtmlUtils.htmlEscape(details == null ? "" : details);
        
        StringBuilder html = new StringBuilder();
        html.append("<h1>로그인 페이지</h1>");

        if (error != null) {
            html.append("<div style='background: #ffebee; color: #c62828; padding: 10px; border-radius: 4px; margin: 10px 0;'>");
            html.append("<strong>로그인 오류:</strong> ").append(safeError);
            if (details != null) {
                html.append("<br><strong>오류 상세:</strong> ").append(safeDetails);
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
