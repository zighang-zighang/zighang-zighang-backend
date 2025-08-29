package com.github.zighang_zighang.global.property;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.hc.core5.http.URIScheme;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.opensearch")
public class OpenSearchProperty {

    @NotNull
    URIScheme scheme;

    @NotBlank
    String host;

    @Min(1)
    @Max(65535)
    @NotNull
    Integer port;

    @NotBlank
    String username;

    @NotBlank
    String password;
}
