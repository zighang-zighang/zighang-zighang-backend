package com.github.zighang_zighang.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String API_TITLE = "직행으로 직행";
    private static final String API_DESCRIPTION = "큐시즘 기업 프로젝트 | 직행 3팀";
    private static final String SECURITY_SCHEME_NAME = "JWT";

    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .components(createComponents())
                .info(createInfo());
    }

    private Components createComponents() {

        return new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme());
    }

    private Info createInfo() {

        return new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(buildProperties.getVersion());
    }

    private SecurityScheme createSecurityScheme() {

        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
