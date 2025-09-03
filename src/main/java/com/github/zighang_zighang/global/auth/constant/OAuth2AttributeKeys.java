package com.github.zighang_zighang.global.auth.constant;

/**
 * OAuth2 사용자 정보 속성 키 상수
 * 휴먼 에러 방지 및 타입 안정성을 위해 하드코딩된 문자열을 상수로 관리
 */
public final class OAuth2AttributeKeys {
    
    // 공통 속성
    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String PICTURE = "picture";
    public static final String PROVIDER = "provider";
    
    // Google 속성
    public static final String SUB = "sub";
    
    // Kakao 속성
    public static final String KAKAO_ACCOUNT = "kakao_account";
    public static final String PROPERTIES = "properties";
    public static final String NICKNAME = "nickname";
    public static final String PROFILE_IMAGE_URL = "profile_image_url";
    
    // Naver 속성
    public static final String RESPONSE = "response";
    public static final String PROFILE_IMAGE = "profile_image";
    
    // 제공자 타입
    public static final String PROVIDER_GOOGLE = "google";
    public static final String PROVIDER_KAKAO = "kakao";
    public static final String PROVIDER_NAVER = "naver";
    
    private OAuth2AttributeKeys() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }
}
