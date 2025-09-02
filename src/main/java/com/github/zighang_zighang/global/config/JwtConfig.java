package com.github.zighang_zighang.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.annotation.PostConstruct;
import java.time.Duration;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    @NotBlank(message = "JWT secret key는 필수입니다")
    private String secretKey;
    
    @NotNull(message = "Access token 만료 시간은 필수입니다")
    private Duration accessTokenExpiration;
    
    @NotNull(message = "Refresh token 만료 시간은 필수입니다")
    private Duration refreshTokenExpiration;
    
    @NotBlank(message = "JWT issuer는 필수입니다")
    private String issuer;
    
    /**
     * Duration 값이 유효한지 검증
     */
    @PostConstruct
    void validateDurations() {
        if (accessTokenExpiration != null && accessTokenExpiration.isNegative()) {
            throw new IllegalStateException("Access token 만료 시간은 양수여야 합니다: " + accessTokenExpiration);
        }
        if (refreshTokenExpiration != null && refreshTokenExpiration.isNegative()) {
            throw new IllegalStateException("Refresh token 만료 시간은 양수여야 합니다: " + refreshTokenExpiration);
        }
        if (accessTokenExpiration != null && accessTokenExpiration.isZero()) {
            throw new IllegalStateException("Access token 만료 시간은 0이 될 수 없습니다");
        }
        if (refreshTokenExpiration != null && refreshTokenExpiration.isZero()) {
            throw new IllegalStateException("Refresh token 만료 시간은 0이 될 수 없습니다");
        }
    }
}
