package com.github.zighang_zighang.global.auth.service;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.domain.user.entity.UserProvider;
import com.github.zighang_zighang.domain.user.service.UserService;
import com.github.zighang_zighang.domain.user.constant.ProviderType;
import com.github.zighang_zighang.global.auth.dto.LoginResponse;
import com.github.zighang_zighang.global.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse oauth2Login(String email, String name, ProviderType providerType, Object providerId) {
        // 사용자 조회 또는 생성
        User user = userService.findUserByEmail(email)
                .orElseGet(() -> userService.createUser(email, name));

        // OAuth2 제공자 정보를 user_provider 테이블에 저장 (중복 체크 후)
        String providerIdStr = providerId != null ? providerId.toString() : null;
        UserProvider userProvider = userService.findUserProviderByProviderIdAndType(providerIdStr, providerType)
                .orElseGet(() -> userService.createUserProvider(user, providerType, providerIdStr));
        
        log.info("OAuth2 제공자 정보 처리: {} - {} ({}) - 기존: {}", 
                user.getEmail(), providerType, providerId, userProvider.getId());

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        log.info("OAuth2 사용자 로그인 성공: {} (제공자: {})", user.getEmail(), providerType);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .name(user.getName())
                .userId(user.getId().toString())
                .expiresIn(3600) // 1시간 (초 단위)
                .build();
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refresh token입니다");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 새로운 access token 생성
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getName());

        log.info("토큰 갱신 성공: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 기존 refresh token 유지
                .email(user.getEmail())
                .name(user.getName())
                .userId(user.getId().toString())
                .expiresIn(3600) // 1시간 (초 단위)
                .build();
    }
}
