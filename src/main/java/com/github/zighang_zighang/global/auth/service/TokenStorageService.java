package com.github.zighang_zighang.global.auth.service;

import com.github.zighang_zighang.global.auth.repository.RefreshTokenRedisRepository;
import com.github.zighang_zighang.global.auth.schema.RefreshToken;
import com.github.zighang_zighang.global.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenStorageService {
    
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final JwtConfig jwtConfig;
    
    /**
     * 사용자 ID로 refresh token을 Redis에 저장 (다중 디바이스 지원)
     */
    public String storeRefreshToken(String userId, String refreshToken, String deviceInfo) {
        // JWT 설정의 refresh token 만료 시간을 밀리초 단위로 사용
        long ttlMillis = jwtConfig.getRefreshTokenExpiration().toMillis();
        
        // 고유한 세션 ID 생성
        String sessionId = UUID.randomUUID().toString();
        
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(sessionId) // sessionId를 id로 사용
                .token(refreshToken)
                .userId(userId)
                .sessionId(sessionId)
                .deviceInfo(deviceInfo != null ? deviceInfo : "Unknown Device")
                .ttl(ttlMillis)
                .build();
        
        refreshTokenRedisRepository.save(refreshTokenEntity);
        log.info("Refresh token을 Redis에 저장했습니다. 사용자 ID: {}, 세션 ID: {}, 디바이스: {}, Expiry: {}ms ({}일)", 
                userId, sessionId, deviceInfo, ttlMillis, ttlMillis / (1000 * 60 * 60 * 24));
        
        return sessionId;
    }
    
    /**
     * 사용자 ID로 refresh token을 Redis에 저장 (기존 호환성)
     */
    public void storeRefreshToken(String userId, String refreshToken) {
        storeRefreshToken(userId, refreshToken, null);
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
     * 특정 사용자가 활성 세션을 가지고 있는지 확인
     */
    public boolean hasTokens(String userId) {
        return !refreshTokenRedisRepository.findByUserId(userId).isEmpty();
    }
    
    /**
     * 사용자의 모든 활성 세션 조회
     */
    public List<RefreshToken> getUserActiveSessions(String userId) {
        return refreshTokenRedisRepository.findByUserId(userId);
    }
    
    /**
     * 특정 세션 ID로 refresh token 조회
     */
    public String getRefreshTokenBySessionId(String sessionId) {
        return refreshTokenRedisRepository.findById(sessionId)
                .map(RefreshToken::token)
                .orElse(null);
    }
    
    /**
     * 특정 세션 ID로 refresh token 삭제 (로그아웃)
     */
    public void removeSession(String sessionId) {
        refreshTokenRedisRepository.deleteById(sessionId);
        log.info("세션 삭제 완료: {}", sessionId);
    }
    
    /**
     * 사용자의 모든 세션 삭제 (전체 로그아웃)
     */
    public void removeAllUserSessions(String userId) {
        List<RefreshToken> userSessions = getUserActiveSessions(userId);
        for (RefreshToken session : userSessions) {
            refreshTokenRedisRepository.deleteById(session.sessionId());
        }
        log.info("사용자 {}의 모든 세션 삭제 완료 ({}개)", userId, userSessions.size());
    }
    
    /**
     * 사용자의 활성 세션 수 반환
     */
    public long getUserActiveSessionCount(String userId) {
        return refreshTokenRedisRepository.findByUserId(userId).size();
    }
}
