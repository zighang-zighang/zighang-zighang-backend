package com.github.zighang_zighang.global.auth.handler;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.service.AuthService;
import com.github.zighang_zighang.global.auth.service.TokenStorageService;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import com.github.zighang_zighang.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final TokenStorageService tokenStorageService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        
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
            // OAuth2 사용자 정보로 JWT 토큰 발급 (Provider 정보 포함)
            LoginResponse tokenResponse = authService.oauth2Login(email, name, providerType, providerId);
            
            // Refresh Token을 Redis에 저장 (userId 기반)
            tokenStorageService.storeRefreshToken(tokenResponse.getUserId(), tokenResponse.getRefreshToken());
            
            // 프론트엔드로 리다이렉트 (Access Token은 URL로, Refresh Token은 userId로 조회)
            String redirectUrl = String.format(
                "https://zighang-zighang-frontend.vercel.app/?accessToken=%s&userId=%s&name=%s&loginSuccess=true",
                java.net.URLEncoder.encode(tokenResponse.getAccessToken(), java.nio.charset.StandardCharsets.UTF_8),
                tokenResponse.getUserId(),
                java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8)
            );
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "알 수 없는 오류";
            String errorRedirectUrl = String.format(
                "https://zighang-zighang-frontend.vercel.app/auth/error?message=%s",
                java.net.URLEncoder.encode(errorMessage, java.nio.charset.StandardCharsets.UTF_8)
            );
            getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
        }
    }

    // OAuth2 제공자별 이메일 추출
    private String getEmailFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 response.email에서 이메일 추출
        Object response = principal.getAttribute("response");
        if (response instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = (java.util.Map<String, Object>) response;
            String email = (String) resp.get("email");
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
        
        // Google의 경우 email에서 이메일 추출
        String email = principal.getAttribute("email");
        if (email != null) {
            return email;
        }
        
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
        
        // Google의 경우 name에서 이름 추출
        String name = principal.getAttribute("name");
        if (name != null) {
            return name;
        }
        
        // 이메일을 이름으로 사용 (fallback)
        String email = getEmailFromPrincipal(principal);
        return email != null ? email.split("@")[0] : "Unknown User";
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
        
        throw new ApiException(AuthExceptionCode.PROVIDER_ID_NOT_FOUND);
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
        
        throw new ApiException(AuthExceptionCode.PROVIDER_TYPE_NOT_FOUND);
    }
}
