package com.github.zighang_zighang.global.auth.handler;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.global.auth.constant.OAuth2AttributeKeys;
import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import com.github.zighang_zighang.global.auth.service.AuthService;
import com.github.zighang_zighang.global.auth.service.TokenStorageService;
import com.github.zighang_zighang.global.auth.util.JwtUtil;
import com.github.zighang_zighang.global.auth.util.RedirectValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final TokenStorageService tokenStorageService;
    private final JwtUtil jwtUtil;
    private final RedirectValidator redirectValidator;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        
        String email = getEmailFromPrincipal(principal);
        String name = getNameFromPrincipal(principal);
        Object providerId = getProviderIdFromPrincipal(principal);
        ProviderType providerType = resolveProviderType(authentication, principal);
        
        try {
            // OAuth2 사용자 정보로 JWT 토큰 발급 (Provider 정보 포함)
            LoginResponse tokenResponse = authService.oauth2Login(email, name, providerType, providerId);
            
            // JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(email, name, tokenResponse.getUserId());
            String refreshToken = jwtUtil.generateRefreshToken(email, name, tokenResponse.getUserId());
            
            // User-Agent에서 디바이스 정보 추출
            String userAgent = request.getHeader("User-Agent");
            String deviceInfo = extractDeviceInfo(userAgent);
            
            // Refresh Token을 Redis에 저장
            String sessionId = tokenStorageService.storeRefreshToken(
                tokenResponse.getUserId(), 
                refreshToken, 
                deviceInfo
            );

            String targetBaseUrl = resolveFrontendBaseUrl(request);
            
            // 프론트엔드로 리다이렉트 (Access Token과 Refresh Token은 fragment로, 세션 ID로 관리)
            String redirectUrl = String.format(
                "%s/auth/callback?accessToken=%s&refreshToken=%s&sessionId=%s&userId=%s&name=%s&loginSuccess=true",
                targetBaseUrl,
                URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                sessionId,
                tokenResponse.getUserId(),
                URLEncoder.encode(name, StandardCharsets.UTF_8)
            );
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);
            
            // 프론트엔드에는 일반화된 에러 코드만 전달 (보안상 안전)
            String errorRedirectUrl = String.format(
                "%s/auth/error#code=%s",
                frontendBaseUrl,
                "OAUTH2_LOGIN_FAILED"
            );
            getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
        }
    }

    private String resolveFrontendBaseUrl(HttpServletRequest request) {
        String redirectUriParam = request.getParameter("redirect_uri");
        if (redirectUriParam != null) {
            String decoded = URLDecoder.decode(redirectUriParam, StandardCharsets.UTF_8);
            if (redirectValidator.isAuthorized(decoded)) {
                return decoded;
            } else {
                log.warn("비허용 redirect_uri 요청 차단됨: {}", decoded);
            }
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("redirect_uri".equals(cookie.getName())) {
                    String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    if (redirectValidator.isAuthorized(decoded)) {
                        return decoded;
                    } else {
                        log.warn("비허용 redirect_uri 요청 차단됨: {}", decoded);
                    }
                }
            }
        }

        return frontendBaseUrl; // 기본값 (application.yaml)
    }

    // OAuth2 제공자별 이메일 추출
    private String getEmailFromPrincipal(OAuth2User principal) {
        String email = principal.getAttribute(OAuth2AttributeKeys.EMAIL);
        if (email == null) {
            // 카카오의 경우 kakao_account.email에서 이메일 추출
            Object kakaoAccount = principal.getAttribute(OAuth2AttributeKeys.KAKAO_ACCOUNT);
            if (kakaoAccount instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> account = (java.util.Map<String, Object>) kakaoAccount;
                email = (String) account.get(OAuth2AttributeKeys.EMAIL);
            }
        }

        if (email == null) {
            throw AuthExceptionCode.EMAIL_NOT_PROVIDED.toException();
        }

        return email;
    }

    // OAuth2 제공자별 이름 추출
    private String getNameFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 name에서 이름 추출 (response가 평탄화됨)
        String name = principal.getAttribute(OAuth2AttributeKeys.NAME);
        if (name != null) {
            return name;
        }
        
        // 카카오의 경우 properties.nickname에서 이름 추출
        Object properties = principal.getAttribute(OAuth2AttributeKeys.PROPERTIES);
        if (properties instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> props = (java.util.Map<String, Object>) properties;
            String nickname = (String) props.get(OAuth2AttributeKeys.NICKNAME);
            if (nickname != null) {
                return nickname;
            }
        }
        
        // Google의 경우 name에서 이름 추출
        if (name != null) {
            return name;
        }
        
        // 이메일을 이름으로 사용 (fallback)
        String email = getEmailFromPrincipal(principal);
        return email != null ? email.split("@")[0] : "Unknown User";
    }

    // OAuth2 제공자별 제공자 ID 추출
    private Object getProviderIdFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 id에서 제공자 ID 추출 (String) - response가 평탄화됨
        Object id = principal.getAttribute(OAuth2AttributeKeys.ID);
        if (id != null) {
            return id;
        }
        
        // 카카오의 경우 id에서 제공자 ID 추출 (Long 또는 String. id가 Long 타입일 수 있음)
        if (id != null) {
            if (id instanceof String) {
                return id;
            } else if (id instanceof Long) {
                return id;
            } else if (id instanceof Number) {
                return id.toString();
            }
        }
        
        // Google의 경우 sub에서 제공자 ID 추출 (String)
        String sub = principal.getAttribute(OAuth2AttributeKeys.SUB);
        if (sub != null) {
            return sub;
        }
        
        throw AuthExceptionCode.PROVIDER_ID_NOT_FOUND.toException();
    }

    // OAuth2 제공자 타입 추출 (우선순위: registrationId > provider 속성)
    private ProviderType resolveProviderType(Authentication authentication, OAuth2User principal) {
        // 1. OAuth2AuthenticationToken의 registrationId 우선 사용 (가장 정확)
        if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauth2) {
            String registrationId = oauth2.getAuthorizedClientRegistrationId();
            if (registrationId != null) {
                switch (registrationId.toLowerCase()) {
                    case "naver":
                        return ProviderType.NAVER;
                    case "kakao":
                        return ProviderType.KAKAO;
                    case "google":
                        return ProviderType.GOOGLE;
                    default:
                        break;
                }
            }
        }

        // 2. 폴백: provider 속성에서 제공자 타입 추출
        String provider = principal.getAttribute(OAuth2AttributeKeys.PROVIDER);
        if (provider != null) {
            switch (provider.toLowerCase()) {
                case OAuth2AttributeKeys.PROVIDER_NAVER:
                    return ProviderType.NAVER;
                case OAuth2AttributeKeys.PROVIDER_KAKAO:
                    return ProviderType.KAKAO;
                case OAuth2AttributeKeys.PROVIDER_GOOGLE:
                    return ProviderType.GOOGLE;
                default:
                    break;
            }
        }
        
        throw AuthExceptionCode.PROVIDER_TYPE_NOT_FOUND.toException();
    }

    // OAuth2 제공자 타입 추출 (기존 방식 - 하위 호환성)
    private ProviderType getProviderTypeFromPrincipal(OAuth2User principal) {
        // provider 속성에서 직접 제공자 타입 추출
        String provider = principal.getAttribute(OAuth2AttributeKeys.PROVIDER);
        if (provider != null) {
            switch (provider.toLowerCase()) {
                case OAuth2AttributeKeys.PROVIDER_NAVER:
                    return ProviderType.NAVER;
                case OAuth2AttributeKeys.PROVIDER_KAKAO:
                    return ProviderType.KAKAO;
                case OAuth2AttributeKeys.PROVIDER_GOOGLE:
                    return ProviderType.GOOGLE;
                default:
                    break;
            }
        }

        throw AuthExceptionCode.PROVIDER_TYPE_NOT_FOUND.toException();
    }
    
    /**
     * User-Agent에서 디바이스 정보 추출
     */
    private String extractDeviceInfo(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown Device";
        }
        
        String deviceInfo = "Unknown Device";
        
        // 모바일 디바이스 감지
        if (userAgent.toLowerCase().contains("mobile") || 
            userAgent.toLowerCase().contains("android") || 
            userAgent.toLowerCase().contains("iphone") || 
            userAgent.toLowerCase().contains("ipad")) {
            deviceInfo = "Mobile Device";
        }
        // 데스크톱 브라우저 감지
        else if (userAgent.toLowerCase().contains("chrome") || 
                 userAgent.toLowerCase().contains("firefox") || 
                 userAgent.toLowerCase().contains("safari") || 
                 userAgent.toLowerCase().contains("edge")) {
            deviceInfo = "Desktop Browser";
        }
        // 기타
        else {
            deviceInfo = "Other Device";
        }
        
        return deviceInfo;
    }
}
