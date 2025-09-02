package com.github.zighang_zighang.global.auth.controller;

import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.service.AuthService;
import com.github.zighang_zighang.global.auth.service.TokenStorageService;
import com.github.zighang_zighang.global.auth.util.JwtUtil;
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
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("Refresh-Token") String refreshTokenHeader) {
        
        // Authorization 헤더에서 Bearer 토큰 추출
        if (!authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity
                .status(401)
                .body(ApiResponse.error(AuthExceptionCode.INVALID_TOKEN.getCode(), 
                                      AuthExceptionCode.INVALID_TOKEN.getMessage()));
        }
        
        String accessToken = authorizationHeader.substring(7);
        
        // Access token 유효성 검증
        if (!jwtUtil.validateAccessToken(accessToken)) {
            return ResponseEntity
                .status(401)
                .body(ApiResponse.error(AuthExceptionCode.INVALID_TOKEN.getCode(), 
                                      AuthExceptionCode.INVALID_TOKEN.getMessage()));
        }
        
        // Refresh token 유효성 검증
        if (!jwtUtil.validateToken(refreshTokenHeader)) {
            return ResponseEntity
                .status(401)
                .body(ApiResponse.error(AuthExceptionCode.INVALID_REFRESH_TOKEN.getCode(), 
                                      AuthExceptionCode.INVALID_REFRESH_TOKEN.getMessage()));
        }
        
        // Access token에서 사용자 정보 추출
        String email = jwtUtil.getEmailFromToken(accessToken);
        if (email == null) {
            return ResponseEntity
                .status(401)
                .body(ApiResponse.error(AuthExceptionCode.INVALID_TOKEN.getCode(), 
                                      AuthExceptionCode.INVALID_TOKEN.getMessage()));
        }
        
        // Redis에서 해당 사용자의 refresh token과 일치하는지 확인
        String storedRefreshToken = tokenStorageService.getRefreshToken(email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshTokenHeader)) {
            return ResponseEntity
                .status(401)
                .body(ApiResponse.error(AuthExceptionCode.REFRESH_TOKEN_NOT_FOUND.getCode(), 
                                      AuthExceptionCode.REFRESH_TOKEN_NOT_FOUND.getMessage()));
        }
        
        // 토큰 갱신
        LoginResponse response = authService.refreshToken(refreshTokenHeader);
        
        // 응답 헤더에 캐시 방지 및 보안 헤더 설정
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .header(HttpHeaders.EXPIRES, "0")
            .body(ApiResponse.ok(response));
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
