package com.github.zighang_zighang.global.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = "com.github.zighang_zighang",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.repository\\..*Redis.*")
)
public class MysqlConfig {

}