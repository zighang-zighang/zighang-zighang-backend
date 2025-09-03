package com.github.zighang_zighang.global.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * OAuth2 사용자 정보 DTO
 * Map 대신 타입 안전한 객체로 사용자 정보를 관리
 */
@Getter
@Builder
public class OAuth2UserInfo {
    
    private final String providerId;
    private final String email;
    private final String name;
    private final String picture;
    private final String provider;

    public String getNameAttributeKey() {
        return switch (provider) {
            case "google" -> "sub";
            case "kakao", "naver" -> "id";
            default -> "id";
        };
    }
}
