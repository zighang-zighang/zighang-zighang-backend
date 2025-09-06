package com.github.zighang_zighang.global.auth.dto;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.global.auth.constant.OAuth2AttributeKeys;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Schema(description = "OAuth2 사용자 정보 DTO")
@Getter
@Builder
public class OAuth2UserInfo {

    @NonNull
    private final String providerId;

    @NonNull
    private final String email;

    @NonNull
    private final String name;

    private final String picture;

    @NonNull
    private final ProviderType provider;

    public String getNameAttributeKey() {
        return switch (provider) {
            case GOOGLE -> OAuth2AttributeKeys.SUB;
            case KAKAO, NAVER -> OAuth2AttributeKeys.ID;
            default -> OAuth2AttributeKeys.ID;
        };
    }
}
