package com.github.zighang_zighang.global.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenStorageService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    // 토큰 만료 시간 설정
    private static final Duration TOKEN_EXPIRY = Duration.ofMinutes(5); // 5분
    
    /**
     * 임시 토큰 ID로 access token을 Redis에 저장
     */
    public void storeAccessToken(String tempTokenId, String accessToken) {
        String redisKey = "access_token:" + tempTokenId;
        redisTemplate.opsForValue().set(redisKey, accessToken, TOKEN_EXPIRY);
        log.info("Access token을 Redis에 저장했습니다. Key: {}, Expiry: {}분", redisKey, TOKEN_EXPIRY.toMinutes());
    }
    
    /**
     * 임시 토큰 ID로 refresh token을 Redis에 저장
     */
    public void storeRefreshToken(String tempTokenId, String refreshToken) {
        String redisKey = "refresh_token:" + tempTokenId;
        redisTemplate.opsForValue().set(redisKey, refreshToken, TOKEN_EXPIRY);
        log.info("Refresh token을 Redis에 저장했습니다. Key: {}, Expiry: {}분", redisKey, TOKEN_EXPIRY.toMinutes());
    }
    
    /**
     * 임시 토큰 ID로 access token을 Redis에서 조회
     */
    public String getAccessToken(String tempTokenId) {
        String redisKey = "access_token:" + tempTokenId;
        String accessToken = redisTemplate.opsForValue().get(redisKey);
        
        if (accessToken != null) {
            log.info("Redis에서 access token을 조회했습니다. Key: {}", redisKey);
            return accessToken;
        }
        
        log.warn("Redis에서 access token을 찾을 수 없습니다. Key: {}", redisKey);
        return null;
    }
    
    /**
     * 임시 토큰 ID로 refresh token을 Redis에서 조회
     */
    public String getRefreshToken(String tempTokenId) {
        String redisKey = "refresh_token:" + tempTokenId;
        String refreshToken = redisTemplate.opsForValue().get(redisKey);
        
        if (refreshToken != null) {
            log.info("Redis에서 refresh token을 조회했습니다. Key: {}", redisKey);
            return refreshToken;
        }
        
        log.warn("Redis에서 refresh token을 찾을 수 없습니다. Key: {}", redisKey);
        return null;
    }
    
    /**
     * access token을 조회한 후 Redis에서 제거 (일회성 사용)
     */
    public String getAndRemoveAccessToken(String tempTokenId) {
        String redisKey = "access_token:" + tempTokenId;
        String accessToken = redisTemplate.opsForValue().get(redisKey);
        
        if (accessToken != null) {
            // 토큰을 Redis에서 제거
            redisTemplate.delete(redisKey);
            log.info("Redis에서 access token을 조회하고 제거했습니다. Key: {}", redisKey);
            return accessToken;
        }
        
        log.warn("Redis에서 access token을 찾을 수 없습니다. Key: {}", redisKey);
        return null;
    }
    
    /**
     * refresh token을 조회한 후 Redis에서 제거 (일회성 사용)
     */
    public String getAndRemoveRefreshToken(String tempTokenId) {
        String redisKey = "refresh_token:" + tempTokenId;
        String refreshToken = redisTemplate.opsForValue().get(redisKey);
        
        if (refreshToken != null) {
            // 토큰을 Redis에서 제거
            redisTemplate.delete(redisKey);
            log.info("Redis에서 refresh token을 조회하고 제거했습니다. Key: {}", redisKey);
            return refreshToken;
        }
        
        log.warn("Redis에서 refresh token을 찾을 수 없습니다. Key: {}", redisKey);
        return null;
    }
    
    /**
     * Redis에 저장된 토큰 개수 반환 (디버깅용)
     */
    public long getStorageSize() {
        return redisTemplate.keys("access_token:*").size() + redisTemplate.keys("refresh_token:*").size();
    }
    
    /**
     * 특정 토큰 ID가 Redis에 존재하는지 확인
     */
    public boolean hasTokens(String tempTokenId) {
        String accessKey = "access_token:" + tempTokenId;
        String refreshKey = "refresh_token:" + tempTokenId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(accessKey)) && 
               Boolean.TRUE.equals(redisTemplate.hasKey(refreshKey));
    }
}
