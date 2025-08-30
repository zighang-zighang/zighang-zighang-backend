package com.github.zighang_zighang.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private String secretKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private String issuer;
}
