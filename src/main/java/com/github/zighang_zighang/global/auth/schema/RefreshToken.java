package com.github.zighang_zighang.global.auth.schema;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@Builder
@RedisHash(value = "refresh_token")
public record RefreshToken(

        @Id
        String id,

        @NotBlank String token,

        @Indexed @NotBlank String userId,

        @Indexed @NotBlank String sessionId,

        String deviceInfo,

        @TimeToLive(unit = TimeUnit.MILLISECONDS)
        long ttl
) {

    /**
     * Redis 키 생성: refresh_token:{userId}:{sessionId}
     */
    public String getRedisKey() {
        return String.format("refresh_token:%s:%s", userId, sessionId);
    }
}
