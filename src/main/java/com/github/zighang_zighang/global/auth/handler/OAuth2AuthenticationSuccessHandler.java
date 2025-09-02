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

        if (log.isDebugEnabled()) {
            log.debug("OAuth2 추출 결과 - providerType={}", providerType);
        }
        
        try {
            // OAuth2 사용자 정보로 JWT 토큰 발급 (Provider 정보 포함)
            LoginResponse tokenResponse = authService.oauth2Login(email, name, providerType, providerId);
            
            // User-Agent에서 디바이스 정보 추출
            String userAgent = request.getHeader("User-Agent");
            String deviceInfo = extractDeviceInfo(userAgent);
            
            // Refresh Token을 Redis에 저장 (userId 기반으로 통일)
            String sessionId = tokenStorageService.storeRefreshToken(
                tokenResponse.getUserId(), 
                tokenResponse.getRefreshToken(), 
                deviceInfo
            );
            
            // 프론트엔드로 리다이렉트 (Access Token은 fragment로, 세션 ID로 관리)
            String redirectUrl = String.format(
                "https://zighang-zighang-frontend.vercel.app/#accessToken=%s&sessionId=%s&userId=%s&name=%s&loginSuccess=true",
                java.net.URLEncoder.encode(tokenResponse.getAccessToken(), java.nio.charset.StandardCharsets.UTF_8),
                sessionId,
                tokenResponse.getUserId(),
                java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8)
            );
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
        } catch (Exception e) {
            // 상세 예외 정보는 서버 로그에만 기록 (보안 강화)
            log.error("OAuth2 로그인 처리 중 오류 발생 - 예외 타입: {}, 메시지: {}", 
                    e.getClass().getSimpleName(), e.getMessage());
            
            // 스택 트레이스는 디버그 레벨에서만 로깅
            if (log.isDebugEnabled()) {
                log.debug("OAuth2 로그인 처리 중 오류 상세 정보", e);
            }
            
            // 프론트엔드에는 일반화된 에러 코드만 전달 (보안상 안전)
            String errorRedirectUrl = String.format(
                "https://zighang-zighang-frontend.vercel.app/auth/error#code=%s",
                "OAUTH2_LOGIN_FAILED"
            );
            getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
        }
    }

    // OAuth2 제공자별 이메일 추출
    private String getEmailFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 email에서 이메일 추출 (response가 평탄화됨)
        String email = principal.getAttribute("email");
        if (email != null) {
            return email;
        }
        
        // 카카오의 경우 kakao_account.email에서 이메일 추출
        Object kakaoAccount = principal.getAttribute("kakao_account");
        if (kakaoAccount instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> account = (java.util.Map<String, Object>) kakaoAccount;
            String kakaoEmail = (String) account.get("email");
            if (kakaoEmail != null) {
                return kakaoEmail;
            }
        }
        
        // Google의 경우 email에서 이메일 추출
        if (email != null) {
            return email;
        }
        
        throw new ApiException(AuthExceptionCode.EMAIL_NOT_PROVIDED);
    }

    // OAuth2 제공자별 이름 추출
    private String getNameFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 name에서 이름 추출 (response가 평탄화됨)
        String name = principal.getAttribute("name");
        if (name != null) {
            return name;
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
        Object id = principal.getAttribute("id");
        if (id != null) {
            return id;
        }
        
        // 카카오의 경우 id에서 제공자 ID 추출 (Long 또는 String)
        // 카카오는 id가 Long 타입일 수 있음
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
        String sub = principal.getAttribute("sub");
        if (sub != null) {
            return sub;
        }
        
        throw new ApiException(AuthExceptionCode.PROVIDER_ID_NOT_FOUND);
    }

    // OAuth2 제공자 타입 추출
    private ProviderType getProviderTypeFromPrincipal(OAuth2User principal) {
        // 네이버의 경우 nickname 속성이 있으면 NAVER (네이버 고유 속성)
        if (principal.getAttribute("nickname") != null) {
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
        
        // 디버깅을 위한 로깅 추가
        log.warn("제공자 타입을 식별할 수 없습니다. 사용 가능한 속성: {}", principal.getAttributes().keySet());
        throw new ApiException(AuthExceptionCode.PROVIDER_TYPE_NOT_FOUND);
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
