package com.github.zighang_zighang.global.auth.util;

import com.github.zighang_zighang.global.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import jakarta.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(String email, String name) {
        return generateToken(email, name, jwtConfig.getAccessTokenExpiration(), "access");
    }
    
    public String generateAccessToken(String email, String name, String userId) {
        return generateTokenWithUserId(email, name, userId, jwtConfig.getAccessTokenExpiration(), "access");
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, null, jwtConfig.getRefreshTokenExpiration(), "refresh");
    }

    private String generateToken(String email, String name, Duration expiration, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        if (name != null) {
            claims.put("name", name);
        }
        claims.put("typ", tokenType); // 토큰 타입 구분

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
    
    private String generateTokenWithUserId(String email, String name, String userId, Duration expiration, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId);
        if (name != null) {
            claims.put("name", name);
        }
        claims.put("typ", tokenType); // 토큰 타입 구분

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Access 토큰 전용 검증 (보안 강화)
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // 토큰 타입이 "access"인지 확인
            String tokenType = claims.get("typ", String.class);
            if (!"access".equals(tokenType)) {
                log.warn("Access 토큰이 아닌 토큰 사용 시도: {}", tokenType);
                return false;
            }
            
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Access 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 타입 검증
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) return false;
            
            String tokenType = claims.get("typ", String.class);
            return "access".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getSubject() : null;
    }
    
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("userId", String.class) : null;
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) return true;
            
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 애플리케이션 시작 시 JWT 시크릿 키 강도 검증
     */
    @PostConstruct
    void validateSecretStrength() {
        byte[] key = jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) { // 256bit (32 bytes)
            String errorMsg = String.format(
                "JWT secret 길이가 HS256에 충분하지 않습니다. 현재: %d bytes, 필요: >=32 bytes", 
                key.length
            );
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        log.info("JWT 시크릿 키 강도 검증 통과: {} bytes", key.length);
    }
}
