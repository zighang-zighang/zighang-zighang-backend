package com.github.zighang_zighang.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
public class BeanConfig {

    @Bean
    public SpelExpressionParser spelExpressionParser() {

        return new SpelExpressionParser();
    }
}
