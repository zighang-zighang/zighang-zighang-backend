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
        
        // 모든 제공자에 대해 일관된 방식으로 처리
        OAuth2User processedUser = processOAuth2User(oauth2User, provider);
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
            
            // kakao_account.profile에서 닉네임과 프로필 이미지 추출
            Object profile = account.get("profile");
            if (profile instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> profileMap = (Map<String, Object>) profile;
                name = (String) profileMap.get("nickname");
                picture = (String) profileMap.get("profile_image_url");
            }
        }

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
        
        // 표준화된 속성으로 평탄화하고 제공자 타입 추가
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("provider", "google"); // 제공자 타입 추가
        
        return new DefaultOAuth2User(
            oauth2User.getAuthorities(),
            attributes,
            "sub"  // Google의 경우 "sub"가 표준
        );
    }
}
