package com.github.zighang_zighang.global.auth.service;

import com.github.zighang_zighang.global.auth.constant.OAuth2AttributeKeys;
import com.github.zighang_zighang.global.auth.dto.OAuth2UserInfo;
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
        return switch (provider) {
            case OAuth2AttributeKeys.PROVIDER_NAVER -> processNaverUser(oauth2User);
            case OAuth2AttributeKeys.PROVIDER_KAKAO -> processKakaoUser(oauth2User);
            case OAuth2AttributeKeys.PROVIDER_GOOGLE -> processGoogleUser(oauth2User);
            default -> oauth2User;
        };
    }
    
    /**
     * 네이버 사용자 정보 처리
     */
    private OAuth2User processNaverUser(OAuth2User oauth2User) {
        Object response = oauth2User.getAttribute(OAuth2AttributeKeys.RESPONSE);
        if (response instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = (Map<String, Object>) response;
            
            OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
                .providerId((String) responseMap.get(OAuth2AttributeKeys.ID))
                .email((String) responseMap.get(OAuth2AttributeKeys.EMAIL))
                .name((String) responseMap.getOrDefault(OAuth2AttributeKeys.NAME, 
                    responseMap.get(OAuth2AttributeKeys.EMAIL)))
                .picture((String) responseMap.get(OAuth2AttributeKeys.PROFILE_IMAGE))
                .provider(OAuth2AttributeKeys.PROVIDER_NAVER)
                .build();
            
            // response를 평탄화하여 attributes로 사용
            Map<String, Object> attributes = new HashMap<>(responseMap);
            attributes.put(OAuth2AttributeKeys.PROVIDER, OAuth2AttributeKeys.PROVIDER_NAVER);
            
            return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                attributes,
                userInfo.getNameAttributeKey()
            );
        }
        return oauth2User;
    }
    
    /**
     * 카카오 사용자 정보 처리
     */
    private OAuth2User processKakaoUser(OAuth2User oauth2User) {
        Long providerId = oauth2User.getAttribute(OAuth2AttributeKeys.ID);
        String email = null, name = null, picture = null;

        Object kakaoAccount = oauth2User.getAttribute(OAuth2AttributeKeys.KAKAO_ACCOUNT);
        if (kakaoAccount instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> account = (Map<String, Object>) kakaoAccount;
            email = (String) account.get(OAuth2AttributeKeys.EMAIL);
            
            // kakao_account.profile에서 닉네임과 프로필 이미지 추출
            Object profile = account.get("profile");
            if (profile instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> profileMap = (Map<String, Object>) profile;
                name = (String) profileMap.get(OAuth2AttributeKeys.NICKNAME);
                picture = (String) profileMap.get(OAuth2AttributeKeys.PROFILE_IMAGE_URL);
            }
        }

        OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
            .providerId(providerId != null ? String.valueOf(providerId) : null)
            .email(email)
            .name(name != null ? name : email)
            .picture(picture)
            .provider(OAuth2AttributeKeys.PROVIDER_KAKAO)
            .build();

        // 표준화된 속성으로 평탄화
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(OAuth2AttributeKeys.ID, userInfo.getProviderId());
        attributes.put(OAuth2AttributeKeys.EMAIL, userInfo.getEmail());
        attributes.put(OAuth2AttributeKeys.NAME, userInfo.getName());
        attributes.put(OAuth2AttributeKeys.PICTURE, userInfo.getPicture());
        attributes.put(OAuth2AttributeKeys.PROVIDER, userInfo.getProvider());
        
        return new DefaultOAuth2User(oauth2User.getAuthorities(), attributes, userInfo.getNameAttributeKey());
    }
    
    /**
     * 구글 사용자 정보 처리
     */
    private OAuth2User processGoogleUser(OAuth2User oauth2User) {
        OAuth2UserInfo userInfo = OAuth2UserInfo.builder()
            .providerId(oauth2User.getAttribute(OAuth2AttributeKeys.SUB))
            .email(oauth2User.getAttribute(OAuth2AttributeKeys.EMAIL))
            .name(oauth2User.getAttribute(OAuth2AttributeKeys.NAME))
            .picture(oauth2User.getAttribute(OAuth2AttributeKeys.PICTURE))
            .provider(OAuth2AttributeKeys.PROVIDER_GOOGLE)
            .build();
        
        // 표준화된 속성으로 평탄화하고 제공자 타입 추가
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put(OAuth2AttributeKeys.PROVIDER, OAuth2AttributeKeys.PROVIDER_GOOGLE);
        
        return new DefaultOAuth2User(
            oauth2User.getAuthorities(),
            attributes,
            userInfo.getNameAttributeKey()
        );
    }
}
