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
        
        log.info("OAuth2 사용자 정보 로드 - Provider: {}", userRequest.getClientRegistration().getRegistrationId());
        log.info("원본 OAuth2 사용자 속성: {}", oauth2User.getAttributes());
        
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
                
                log.info("네이버 사용자 정보 - ID: {}, Email: {}, Name: {}, Picture: {}", 
                        providerId, email, name, picture);
                
                // nameAttributeKey를 "response"로 설정 (application.yaml의 user-name-attribute와 일치)
                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    oauth2User.getAttributes(),  // 원본 속성 그대로 유지
                    "response"  // nameAttributeKey를 "response"로 설정
                );
            }
        }
        
        // 다른 제공자들은 그대로 반환
        return oauth2User;
    }
}
