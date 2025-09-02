package com.github.zighang_zighang.global.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        log.info("OAuth2 사용자 정보 로드 - Provider: {}", provider);

        // 디버그 레벨에서만 속성 키 목록 로깅 (민감한 값은 제외)
        if (log.isDebugEnabled()) {
            log.debug("OAuth2 속성 키: {}", oauth2User.getAttributes().keySet());
        }
        
        // 모든 제공자에 대해 일관된 방식으로 처리
        OAuth2User processedUser = processOAuth2User(oauth2User, provider);
        
        log.info("OAuth2 사용자 정보 처리 완료 - Provider: {}", provider);
        return processedUser;
    }
    
    /**
     * OAuth2 제공자별 사용자 정보 처리
     */
    private OAuth2User processOAuth2User(OAuth2User oauth2User, String provider) {
        switch (provider) {
            case "naver":
                return processNaverUser(oauth2User);
            case "kakao":
                return processKakaoUser(oauth2User);
            case "google":
                return processGoogleUser(oauth2User);
            default:
                log.warn("지원하지 않는 OAuth2 제공자: {}", provider);
                return oauth2User;
        }
    }
    
    /**
     * 네이버 사용자 정보 처리
     */
    private OAuth2User processNaverUser(OAuth2User oauth2User) {
        Object response = oauth2User.getAttribute("response");
        if (response instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = (Map<String, Object>) response;
            
            String providerId = (String) responseMap.get("id");
            String email = (String) responseMap.get("email");
            String name = (String) responseMap.getOrDefault("name", email);
            String picture = (String) responseMap.get("profile_image");
            
            log.info("네이버 사용자 정보 - ID: {}, Email: {}, Name: {}", 
                    providerId, maskEmail(email), maskName(name));
            
            // response를 평탄화하여 attributes로 사용하고, nameAttributeKey는 "id"로 지정
            Map<String, Object> attributes = new HashMap<>(responseMap);
            attributes.put("provider", "naver"); // 제공자 타입 추가
            return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                attributes,
                "id"  // 표준화된 nameAttributeKey
            );
        }
        return oauth2User;
    }
    
    /**
     * 카카오 사용자 정보 처리
     */
        private OAuth2User processKakaoUser(OAuth2User oauth2User) {
        Long providerId = oauth2User.getAttribute("id");
        String email = null, name = null, picture = null;

        Object kakaoAccount = oauth2User.getAttribute("kakao_account");
        if (kakaoAccount instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> account = (Map<String, Object>) kakaoAccount;
            email = (String) account.get("email");
        }
        Object properties = oauth2User.getAttribute("properties");
        if (properties instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> props = (Map<String, Object>) properties;
            name = (String) props.get("nickname");
            picture = (String) props.get("profile_image");
        }

        log.debug("카카오 사용자 정보 - ID: {}, Email: {}, Name: {}", 
                providerId, maskEmail(email), maskName(name));

        // 표준화된 속성으로 평탄화
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", providerId != null ? String.valueOf(providerId) : null);
        attributes.put("email", email);
        attributes.put("name", name != null ? name : email);
        attributes.put("picture", picture);
        attributes.put("provider", "kakao"); // 제공자 타입 추가
        
        return new DefaultOAuth2User(oauth2User.getAuthorities(), attributes, "id");
    }
    
    /**
     * 구글 사용자 정보 처리
     */
    private OAuth2User processGoogleUser(OAuth2User oauth2User) {
        String providerId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        log.info("구글 사용자 정보 - ID: {}, Email: {}, Name: {}", 
                providerId, maskEmail(email), maskName(name));
        
        // 표준화된 속성으로 평탄화하고 제공자 타입 추가
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("provider", "google"); // 제공자 타입 추가
        
        return new DefaultOAuth2User(
            oauth2User.getAuthorities(),
            attributes,
            "sub"  // Google의 경우 "sub"가 표준
        );
    }
    
    /**
     * 이메일 주소 마스킹
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "N/A";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email; // 너무 짧은 경우 그대로 반환
        }
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        String maskedLocal = localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1);
        return maskedLocal + domain;
    }
    
    /**
     * 이름 마스킹
     */
    private String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return "N/A";
        }
        if (name.length() <= 2) {
            return name; // 너무 짧은 경우 그대로 반환
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1);
    }
}
