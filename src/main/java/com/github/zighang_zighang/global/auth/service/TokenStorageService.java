package com.github.zighang_zighang.global.auth.service;

import com.github.zighang_zighang.global.auth.repository.RefreshTokenRedisRepository;
import com.github.zighang_zighang.global.auth.schema.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenStorageService {
    
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    
    // 토큰 만료 시간 설정 (시간 단위)
    private static final long TOKEN_EXPIRY_HOURS = 24; // 24시간
    
    /**
     * 임시 토큰 ID로 access token을 Redis에 저장
     */
    public void storeAccessToken(String tempTokenId, String accessToken) {
        RefreshToken accessTokenEntity = RefreshToken.builder()
                .id(tempTokenId + "_access") // access token용 고유 ID
                .token(accessToken)
                .userId(tempTokenId)
                .ttl(1) // access token은 1시간만 유지
                .build();
        
        refreshTokenRedisRepository.save(accessTokenEntity);
        log.info("Access token을 Redis에 저장했습니다. ID: {}, Expiry: 1시간", tempTokenId);
    }
    
    /**
     * 임시 토큰 ID로 refresh token을 Redis에 저장
     */
    public void storeRefreshToken(String tempTokenId, String refreshToken) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(tempTokenId) // tempTokenId를 직접 id로 사용
                .token(refreshToken)
                .userId(tempTokenId) // tempTokenId를 userId로 사용
                .ttl(TOKEN_EXPIRY_HOURS)
                .build();
        
        refreshTokenRedisRepository.save(refreshTokenEntity);
        log.info("Refresh token을 Redis에 저장했습니다. ID: {}, Expiry: {}시간", tempTokenId, TOKEN_EXPIRY_HOURS);
    }
    
    /**
     * 임시 토큰 ID로 access token을 Redis에서 조회
     */
    public String getAccessToken(String tempTokenId) {
        // Access token은 임시로만 사용되므로 별도 저장하지 않음
        log.warn("Access token은 임시 저장되지 않습니다. ID: {}", tempTokenId);
        return null;
    }
    
    /**
     * 임시 토큰 ID로 refresh token을 Redis에서 조회
     */
    public String getRefreshToken(String tempTokenId) {
        return refreshTokenRedisRepository.findById(tempTokenId)
                .map(RefreshToken::token)
                .orElse(null);
    }
    
    /**
     * access token을 조회한 후 Redis에서 제거 (일회성 사용)
     */
    public String getAndRemoveAccessToken(String tempTokenId) {
        return refreshTokenRedisRepository.findById(tempTokenId + "_access")
                .map(refreshToken -> {
                    refreshTokenRedisRepository.delete(refreshToken);
                    log.info("Redis에서 access token을 조회하고 제거했습니다. ID: {}", tempTokenId);
                    return refreshToken.token();
                })
                .orElse(null);
    }
    
    /**
     * refresh token을 조회한 후 Redis에서 제거 (일회성 사용)
     */
    public String getAndRemoveRefreshToken(String tempTokenId) {
        return refreshTokenRedisRepository.findById(tempTokenId)
                .map(refreshToken -> {
                    refreshTokenRedisRepository.delete(refreshToken);
                    log.info("Redis에서 refresh token을 조회하고 제거했습니다. ID: {}", tempTokenId);
                    return refreshToken.token();
                })
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
    public boolean hasTokens(String tempTokenId) {
        boolean hasAccessToken = refreshTokenRedisRepository.findById(tempTokenId + "_access").isPresent();
        boolean hasRefreshToken = refreshTokenRedisRepository.findById(tempTokenId).isPresent();
        return hasAccessToken && hasRefreshToken;
    }
}
