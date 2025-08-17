package com.github.zighang_zighang.global.property;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@ToString
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperty {

    @NotBlank
    String host;

    @Min(1)
    @Max(65535)
    @NotNull
    Integer port;

    @NotNull
    String username;

    @Nullable
    String password;

    @Min(0)
    @NotNull
    Integer database;
}
