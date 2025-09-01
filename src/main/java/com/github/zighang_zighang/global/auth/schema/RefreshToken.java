package com.github.zighang_zighang.global.auth.schema;

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

        @Indexed
        String token,

        String userId,

        @TimeToLive(unit = TimeUnit.HOURS)
        long ttl
) {

}
