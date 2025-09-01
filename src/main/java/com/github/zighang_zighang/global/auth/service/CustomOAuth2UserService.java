package com.github.zighang_zighang.global.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        log.info("OAuth2 사용자 정보 로드 - Provider: {}", userRequest.getClientRegistration().getRegistrationId());

        // 디버그 레벨에서만 속성 키 목록 로깅 (민감한 값은 제외)
        if (log.isDebugEnabled()) {
            log.debug("OAuth2 속성 키: {}", oauth2User.getAttributes().keySet());
        }
        
        // 네이버의 경우 response 객체에서 id를 추출하여 nameAttributeKey로 설정
        if ("naver".equals(userRequest.getClientRegistration().getRegistrationId())) {
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
                
                // nameAttributeKey를 "response"로 설정 (application.yaml의 user-name-attribute와 일치)
                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    oauth2User.getAttributes(),  // 원본 속성 그대로 유지
                    "response"  // nameAttributeKey를 "response"로 설정
                );
            }
        }
        
        // 카카오의 경우
        if ("kakao".equals(userRequest.getClientRegistration().getRegistrationId())) {
            Long providerId = oauth2User.getAttribute("id");  // Long으로 직접 캐스팅
            
            String email = null;
            Object kakaoAccount = oauth2User.getAttribute("kakao_account");
            if (kakaoAccount instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> account = (Map<String, Object>) kakaoAccount;
                email = (String) account.get("email");
            }
            String name = null;
            Object properties = oauth2User.getAttribute("properties");
            if (properties instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> props = (Map<String, Object>) properties;
                name = (String) props.get("nickname");
            }
            
            log.info("카카오 사용자 정보 - ID: {}, Email: {}, Name: {}", providerId, maskEmail(email), maskName(name));
            
            // nameAttributeKey를 "id"로 설정 (application.yaml의 user-name-attribute와 일치)
            return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                oauth2User.getAttributes(),  // 원본 속성 그대로 유지
                "id"  // nameAttributeKey를 "id"로 설정
            );
        }
        
        // 구글의 경우
        if ("google".equals(userRequest.getClientRegistration().getRegistrationId())) {
            String providerId = oauth2User.getAttribute("sub");
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            
            log.info("구글 사용자 정보 - ID: {}, Email: {}, Name: {}", providerId, maskEmail(email), maskName(name));
        }
        
        // 모든 제공자에 대해 로깅
        log.info("OAuth2 사용자 정보 로드 완료 - Provider: {}", userRequest.getClientRegistration().getRegistrationId());
        
        // 다른 제공자들은 그대로 반환
        return oauth2User;
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
