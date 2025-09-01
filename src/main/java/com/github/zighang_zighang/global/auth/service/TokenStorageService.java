package com.github.zighang_zighang.global.auth.service;

import com.github.zighang_zighang.global.auth.repository.RefreshTokenRedisRepository;
import com.github.zighang_zighang.global.auth.schema.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenStorageService {
    
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    
    // 토큰 만료 시간 설정 (시간 단위)
    private static final long TOKEN_EXPIRY_HOURS = 24; // 24시간
    
    /**
     * 사용자 ID로 refresh token을 Redis에 저장
     */
    public void storeRefreshToken(String userId, String refreshToken) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(userId) // userId를 id로 사용
                .token(refreshToken)
                .userId(userId)
                .ttl(TOKEN_EXPIRY_HOURS)
                .build();
        
        refreshTokenRedisRepository.save(refreshTokenEntity);
        log.info("Refresh token을 Redis에 저장했습니다. 사용자 ID: {}, Expiry: {}시간", userId, TOKEN_EXPIRY_HOURS);
    }
    
    /**
     * 사용자 ID로 refresh token을 Redis에서 조회
     */
    public String getRefreshToken(String userId) {
        return refreshTokenRedisRepository.findById(userId)
                .map(RefreshToken::token)
                .orElse(null);
    }
    
    /**
     * Redis에 저장된 토큰 개수 반환 (디버깅용)
     */
    public long getStorageSize() {
        return refreshTokenRedisRepository.count();
    }

    /**
     * 특정 토큰 ID가 Redis에 존재하는지 확인
     */
    public boolean hasTokens(String userId) {
        return refreshTokenRedisRepository.findByUserId(userId).isPresent();
    }
}
