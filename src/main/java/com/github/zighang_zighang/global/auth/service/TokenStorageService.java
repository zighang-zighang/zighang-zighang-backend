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
        // 입력값 검증
        validateInput(userId, refreshToken);
        
                // JWT 설정의 refresh token 만료 시간을 '초' 단위로 사용 (Spring Data Redis @TimeToLive 기본 단위는 초)
        var exp = jwtConfig.getRefreshTokenExpiration();
        long ttlSeconds = exp.toSeconds();

        // 고유한 세션 ID 생성
        String sessionId = UUID.randomUUID().toString();

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(sessionId) // sessionId를 id로 사용
                .token(refreshToken)
                .userId(userId)
                .sessionId(sessionId)
                .deviceInfo(deviceInfo != null ? deviceInfo : "Unknown Device")
                .ttl(ttlSeconds)
                .build();

        refreshTokenRedisRepository.save(refreshTokenEntity);
        log.info("Refresh token을 Redis에 저장했습니다. 키: {}, 사용자 ID: {}, 세션 ID: {}, 디바이스: {}, Expiry: {}s ({}일)",
                refreshTokenEntity.getRedisKey(), userId, sessionId, deviceInfo, ttlSeconds, exp.toDays());
        
        return sessionId;
    }
    
    /**
     * 사용자 ID로 refresh token을 Redis에 저장 (기존 호환성)
     * 다중 세션 지원을 위해 내부적으로 세션 ID 생성
     */
    public void storeRefreshToken(String userId, String refreshToken) {
        // 다중 세션 지원을 위해 기본 디바이스 정보로 세션 ID 생성
        String sessionId = storeRefreshToken(userId, refreshToken, "Default Device");
        log.info("기존 호환성 메서드로 토큰 저장 완료. 세션 ID: {}", sessionId);
    }
    
    /**
     * 입력값 검증
     */
    private void validateInput(String userId, String refreshToken) {
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
        org.springframework.util.Assert.hasText(refreshToken, "refreshToken must not be blank");
    }
    
    /**
     * 사용자 ID로 refresh token을 Redis에서 조회 (첫 번째 활성 세션)
     */
    public String getRefreshToken(String userId) {
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
        List<RefreshToken> userSessions = refreshTokenRedisRepository.findByUserId(userId);
        return userSessions.isEmpty() ? null : userSessions.get(0).token();
    }
    
    /**
     * 사용자 ID와 토큰 값으로 일치하는 refresh token 조회
     */
    public String getRefreshTokenByUserIdAndToken(String userId, String refreshToken) {
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
        org.springframework.util.Assert.hasText(refreshToken, "refreshToken must not be blank");
        
        List<RefreshToken> userSessions = refreshTokenRedisRepository.findByUserId(userId);
        
        // 사용자의 모든 세션에서 요청한 토큰과 일치하는 토큰 찾기
        for (RefreshToken session : userSessions) {
            if (refreshToken.equals(session.token())) {
                return session.token();
            }
        }
        
        return null; // 일치하는 토큰을 찾지 못함
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
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
        return !refreshTokenRedisRepository.findByUserId(userId).isEmpty();
    }
    
    /**
     * 사용자의 모든 활성 세션 조회
     */
    public List<RefreshToken> getUserActiveSessions(String userId) {
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
        return refreshTokenRedisRepository.findByUserId(userId);
    }
    
    /**
     * 특정 세션 ID로 refresh token 조회
     */
    public String getRefreshTokenBySessionId(String sessionId) {
        org.springframework.util.Assert.hasText(sessionId, "sessionId must not be blank");
        return refreshTokenRedisRepository.findById(sessionId)
                .map(RefreshToken::token)
                .orElse(null);
    }
    
    /**
     * 특정 세션 ID로 refresh token 삭제 (로그아웃)
     */
    public void removeSession(String sessionId) {
        org.springframework.util.Assert.hasText(sessionId, "sessionId must not be blank");
        refreshTokenRedisRepository.deleteById(sessionId);
        log.info("세션 삭제 완료: {}", sessionId);
    }
    
    /**
     * 사용자의 모든 세션 삭제 (전체 로그아웃)
     */
    public void removeAllUserSessions(String userId) {
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
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
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
        return refreshTokenRedisRepository.findByUserId(userId).size();
    }
    
    /**
     * 특정 사용자의 모든 세션 정보를 상세하게 출력 (디버깅용)
     */
    public void debugUserSessions(String userId) {
        org.springframework.util.Assert.hasText(userId, "userId must not be blank");
        log.info("=== 사용자 {}의 세션 정보 ===", userId);
        
        List<RefreshToken> userSessions = getUserActiveSessions(userId);
        
        if (userSessions.isEmpty()) {
            log.info("활성 세션이 없습니다.");
        } else {
            for (int i = 0; i < userSessions.size(); i++) {
                RefreshToken session = userSessions.get(i);
                log.info("세션 {}: SessionID={}, Device={}, TTL={}ms ({}일)", 
                        i + 1, 
                        session.sessionId(), 
                        session.deviceInfo(),
                        session.ttl(),
                        session.ttl() / (1000 * 1000 * 60 * 60 * 24));
            }
            log.info("총 {}개의 활성 세션이 있습니다.", userSessions.size());
        }
        log.info("===============================");
    }
    
    /**
     * Redis에 저장된 모든 토큰 정보 출력 (디버깅용)
     */
    public void debugAllTokens() {
        log.info("=== Redis에 저장된 모든 토큰 정보 ===");
        
        Iterable<RefreshToken> allTokens = refreshTokenRedisRepository.findAll();
        long count = 0;
        
        for (RefreshToken token : allTokens) {
            count++;
            log.info("토큰 {}: ID={}, UserID={}, SessionID={}, Device={}, TTL={}ms ({}일)", 
                    count, 
                    token.id(), 
                    token.userId(), 
                    token.sessionId(), 
                    token.deviceInfo(),
                    token.ttl(),
                    token.ttl() / (1000 * 1000 * 60 * 60 * 24));
        }
        
        log.info("총 {}개의 토큰이 Redis에 저장되어 있습니다.", count);
        log.info("=========================================");
    }
}
