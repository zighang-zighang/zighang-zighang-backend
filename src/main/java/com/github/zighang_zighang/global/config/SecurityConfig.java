package com.github.zighang_zighang.global.config;

import com.github.zighang_zighang.global.auth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화 (개발용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/oauth2/**", "/error").permitAll()  // 공개 접근 허용
                .anyRequest().authenticated()  // 나머지는 인증 필요
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/login") // 커스텀 로그인 페이지
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/login/oauth2/code/*")  // 리다이렉트 URI 패턴 명시
                )
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                .defaultSuccessUrl("/auth/", true) // 로그인 성공 시 리다이렉트
                .failureUrl("/auth/login?error=true") // 로그인 실패 시 리다이렉트
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/auth/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            );

        return http.build();
    }
}
