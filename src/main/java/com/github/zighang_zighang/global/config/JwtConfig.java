package com.github.zighang_zighang.global.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    @NotBlank(message = "JWT secret key는 필수입니다")
    private String secretKey;

    @NotNull(message = "Access token 만료 시간은 필수입니다")
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration accessTokenExpiration;

    @NotNull(message = "Refresh token 만료 시간은 필수입니다")
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration refreshTokenExpiration;

    @NotBlank(message = "JWT issuer는 필수입니다")
    private String issuer;

}
