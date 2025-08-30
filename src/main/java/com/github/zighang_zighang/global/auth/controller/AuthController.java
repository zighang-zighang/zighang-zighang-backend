package com.github.zighang_zighang.global.auth.controller;

import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.service.AuthService;
import com.github.zighang_zighang.global.auth.service.TokenStorageService;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import com.github.zighang_zighang.global.exception.ApiException;
import com.github.zighang_zighang.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final TokenStorageService tokenStorageService;

    // OAuth2 로그인 성공 처리
    @GetMapping("/auth/oauth2-success")
    public void oauth2Success(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) throws IOException {
        if (principal == null) {
            response.sendRedirect("https://zighang-zighang-frontend.vercel.app/auth/error?message=인증 정보를 찾을 수 없습니다");
            return;
        }

        // 디버깅을 위한 로깅 추가
        log.info("OAuth2 로그인 성공 - Principal: {}", principal);
        log.info("OAuth2 사용자 속성들: {}", principal.getAttributes());
        
        String email = getEmailFromPrincipal(principal);
        String name = getNameFromPrincipal(principal);
        
        log.info("추출된 이메일: {}, 이름: {}", email, name);
        
        try {
            // OAuth2 사용자 정보로 JWT 토큰 발급
            LoginResponse tokenResponse = authService.oauth2Login(email, name);
            
            // 토큰을 Redis에 개별적으로 임시 저장 (프론트엔드에서 조회할 수 있도록)
            String tempTokenId = java.util.UUID.randomUUID().toString();
            tokenStorageService.storeAccessToken(tempTokenId, tokenResponse.getAccessToken());
            tokenStorageService.storeRefreshToken(tempTokenId, tokenResponse.getRefreshToken());
            
            // 프론트엔드로 리다이렉트 (tempTokenId 포함)
            String redirectUrl = String.format(
                "https://zighang-zighang-frontend.vercel.app/?tempTokenId=%s&email=%s&name=%s&loginSuccess=true",
                tempTokenId,
                java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8),
                java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8)
            );
            
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "알 수 없는 오류";
            response.sendRedirect("https://zighang-zighang-frontend.vercel.app/auth/error?message=" + 
                               java.net.URLEncoder.encode(errorMessage, java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    // OAuth2 제공자별 이메일 추출
    private String getEmailFromPrincipal(OAuth2User principal) {
        // 카카오의 경우 kakao_account.email에서 이메일 추출
        Object kakaoAccount = principal.getAttribute("kakao_account");
        if (kakaoAccount instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> account = (java.util.Map<String, Object>) kakaoAccount;
            String email = (String) account.get("email");
            if (email != null) {
                return email;
            }
        }
        
        // Google의 경우 email에서 추출
        String email = principal.getAttribute("email");
        if (email != null) {
            return email;
        }
        
        // 이메일이 없는 경우 ApiException 발생 (이메일은 필수)
        throw new ApiException(AuthExceptionCode.EMAIL_NOT_PROVIDED);
    }

    // OAuth2 제공자별 이름 추출
    private String getNameFromPrincipal(OAuth2User principal) {
        // 카카오의 경우 properties.nickname에서 이름 추출
        Object properties = principal.getAttribute("properties");
        if (properties instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> props = (java.util.Map<String, Object>) properties;
            String nickname = (String) props.get("nickname");
            if (nickname != null) {
                return nickname;
            }
        }
        
        // Google의 경우 name에서 추출
        String name = principal.getAttribute("name");
        if (name != null) {
            return name;
        }
        
        // 이름이 없는 경우 기본값 반환 (이름은 선택적)
        return "Unknown User";
    }

    // 임시 토큰 ID로 JWT 토큰 조회 (프론트엔드에서 호출)
    @GetMapping("/auth/token/{tempTokenId}")
    public ApiResponse<String> getTokenByTempId(@PathVariable String tempTokenId, HttpServletResponse response) {
        try {
            // 저장소에서 tempTokenId로 실제 토큰 조회
            String accessToken = tokenStorageService.getAndRemoveAccessToken(tempTokenId);
            String refreshToken = tokenStorageService.getAndRemoveRefreshToken(tempTokenId);
            
            if (accessToken != null && refreshToken != null) {
                // 토큰을 헤더에 설정
                response.setHeader("Authorization", "Bearer " + accessToken);
                response.setHeader("Refresh-Token", refreshToken);
                
                // 응답은 성공 메시지만
                return ApiResponse.ok("로그인 성공");
            } else {
                return ApiResponse.error("TOKEN_NOT_FOUND", "토큰을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ApiResponse.error("TOKEN_NOT_FOUND", "토큰을 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @PostMapping("/auth/refresh")
    public ApiResponse<LoginResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        LoginResponse response = authService.refreshToken(refreshToken);
        return ApiResponse.ok(response);
    }

    @GetMapping("/login")
    public String login() {
        return """
            <h1>로그인 페이지</h1>
            <p>소셜 로그인으로 로그인하세요</p>
            <p><a href="/oauth2/authorization/google" style="background: #4285f4; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 5px;">Google로 로그인</a></p>
            <p><a href="/oauth2/authorization/kakao" style="background: #FEE500; color: #000; padding: 10px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 5px;">카카오로 로그인</a></p>
            """;
    }
}
