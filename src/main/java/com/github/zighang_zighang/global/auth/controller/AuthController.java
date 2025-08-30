package com.github.zighang_zighang.global.auth.controller;

import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.service.AuthService;
import com.github.zighang_zighang.global.auth.service.TokenStorageService;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import com.github.zighang_zighang.global.exception.ApiException;
import com.github.zighang_zighang.global.response.ApiResponse;
import com.github.zighang_zighang.domain.user.constant.ProviderType;
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
        Object providerId = getProviderIdFromPrincipal(principal);
        ProviderType providerType = getProviderTypeFromPrincipal(principal);
        
        log.info("추출된 이메일: {}, 이름: {}, 제공자 ID: {}, 제공자 타입: {}", 
                email, name, providerId, providerType);
        
        try {
            // OAuth2 사용자 정보로 JWT 토큰 발급 (제공자 정보 포함)
            LoginResponse tokenResponse = authService.oauth2Login(email, name, providerType, providerId);
            
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
        // 네이버의 경우 response.email에서 이메일 추출
        Object response = principal.getAttribute("response");
        log.info("네이버 response 속성: {}", response);
        
        if (response instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = (java.util.Map<String, Object>) response;
            log.info("네이버 response 맵: {}", resp);
            
            String email = (String) resp.get("email");
            log.info("네이버에서 추출한 이메일: {}", email);
            
            if (email != null) {
                return email;
            }
        }
        
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
        // 네이버의 경우 response.name에서 이름 추출
        Object response = principal.getAttribute("response");
        if (response instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = (java.util.Map<String, Object>) response;
            String name = (String) resp.get("name");
            if (name != null) {
                return name;
            }
        }
        
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

    // OAuth2 제공자별 제공자 ID 추출
    private Object getProviderIdFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 response.id에서 제공자 ID 추출 (String)
        Object response = principal.getAttribute("response");
        if (response instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = (java.util.Map<String, Object>) response;
            String id = (String) resp.get("id");
            if (id != null) {
                return id;
            }
        }
        
        // 카카오의 경우 id에서 제공자 ID 추출 (Long)
        Long id = principal.getAttribute("id");
        if (id != null) {
            return id;
        }
        
        // Google의 경우 sub에서 제공자 ID 추출 (String)
        String sub = principal.getAttribute("sub");
        if (sub != null) {
            return sub;
        }
        
        throw new ApiException(AuthExceptionCode.EMAIL_NOT_PROVIDED);
    }

    // OAuth2 제공자 타입 추출
    private ProviderType getProviderTypeFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 response 속성이 있으면 NAVER
        if (principal.getAttribute("response") != null) {
            return ProviderType.NAVER;
        }
        
        // 카카오의 경우 kakao_account 속성이 있으면 KAKAO
        if (principal.getAttribute("kakao_account") != null) {
            return ProviderType.KAKAO;
        }
        
        // Google의 경우 sub 속성이 있으면 GOOGLE
        if (principal.getAttribute("sub") != null) {
            return ProviderType.GOOGLE;
        }
        
        throw new ApiException(AuthExceptionCode.EMAIL_NOT_PROVIDED);
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
