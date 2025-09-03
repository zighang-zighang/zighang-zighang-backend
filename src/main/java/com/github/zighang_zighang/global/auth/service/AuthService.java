package com.github.zighang_zighang.global.auth.service;

import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.service.UserService;
import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.dto.TokenRefreshResponse;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import com.github.zighang_zighang.global.auth.util.JwtUtil;
import com.github.zighang_zighang.global.config.JwtConfig;
import com.github.zighang_zighang.global.exception.ApiException;
import com.github.zighang_zighang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final TokenStorageService tokenStorageService;

    public ResponseEntity<ApiResponse<LoginResponse>> handleRefreshToken(String refreshTokenHeader) {
        if (refreshTokenHeader == null || refreshTokenHeader.isBlank()) {
            throw new ApiException(AuthExceptionCode.TOKEN_NOT_FOUND);
        }

        TokenRefreshResponse tokenResponse = this.refreshToken(refreshTokenHeader);

        return ResponseEntity
                .ok()
                .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
                .header("Refresh-Token", tokenResponse.getRefreshToken())
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(ApiResponse.ok(tokenResponse.getUserInfo()));
    }


    @Transactional
    public LoginResponse oauth2Login(String email, String name, ProviderType providerType, Object providerId) {
        // 입력 검증 및 이메일 정규화
        if (email == null || email.isBlank() || providerType == null) {
            throw new ApiException(AuthExceptionCode.OAUTH2_FAILURE);
        }
        String normalizedEmail = email.trim().toLowerCase(java.util.Locale.ROOT);
        
        // 사용자 조회 또는 생성
        User user = userService.findUserByEmail(normalizedEmail)
                .orElseGet(() -> userService.createUser(normalizedEmail, name));

        // OAuth2 제공자 정보를 user_provider 테이블에 저장 (보안 검증 포함)
        if (providerId == null) {
            throw new ApiException(AuthExceptionCode.OAUTH2_FAILURE);
        }
        String providerIdStr = providerId.toString();
        Optional<UserProvider> upOpt = userService.findUserProviderByProviderIdAndType(providerIdStr, providerType);
        UserProvider userProvider = upOpt
            .map(up -> {
                if (!up.getUser().getId().equals(user.getId())) {
                    throw new ApiException(AuthExceptionCode.AUTHENTICATION_FAILED); // 교차 링크 차단
                }
                return up;
            })
            .orElseGet(() -> {
                UserProvider newProvider = userService.createUserProvider(user, providerType, providerIdStr);
                return newProvider;
            });

        return LoginResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .userId(user.getId().toString())
                .expiresIn((int) jwtConfig.getAccessTokenExpiration().toSeconds())
                .build();
    }

    /**
     * Refresh Token으로 새로운 Access Token과 Refresh Token 발급 (토큰 회전)
     */
    public TokenRefreshResponse refreshToken(String refreshToken) {
        // 1. refresh token 타입 검증 (typ=refresh 강제)
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ApiException(AuthExceptionCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new ApiException(AuthExceptionCode.USER_NOT_FOUND));

        // 2. 세션 바인딩: Redis에 저장된 refresh token과 대조
        boolean matched = tokenStorageService.getUserActiveSessions(userId)
                .stream().anyMatch(rt -> refreshToken.equals(rt.token()));
        if (!matched) {
            log.warn("Redis에 저장되지 않은 refresh token 사용 시도: userId={}, email={}", userId, email);
            throw new ApiException(AuthExceptionCode.INVALID_REFRESH_TOKEN);
        }

        // 3. 토큰 회전: 새로운 access token과 refresh token 생성
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName(), user.getId().toString());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getName(), user.getId().toString());

        // 4. 기존 refresh token 무효화 (Redis에서 제거)
        tokenStorageService.getUserActiveSessions(userId)
                .stream()
                .filter(rt -> refreshToken.equals(rt.token()))
                .findFirst()
                .ifPresent(rt -> tokenStorageService.removeSession(rt.sessionId()));

        // 5. 새로운 refresh token을 Redis에 저장
        String sessionId = tokenStorageService.storeRefreshToken(userId, newRefreshToken, "Token Refresh");

        log.info("토큰 회전 성공: {} (기존 토큰 무효화, 새 토큰 발급)", user.getEmail());

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .sessionId(sessionId)
                .userInfo(LoginResponse.builder()
                        .email(user.getEmail())
                        .name(user.getName())
                        .userId(user.getId().toString())
                        .expiresIn((int) jwtConfig.getAccessTokenExpiration().toSeconds())
                        .build())
                .build();
    }
}
