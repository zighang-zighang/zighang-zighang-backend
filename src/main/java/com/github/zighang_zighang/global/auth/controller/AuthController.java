package com.github.zighang_zighang.global.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return "안녕하세요! " + principal.getAttribute("name") + "님, Google 로그인에 성공했습니다!";
        }
        return "안녕하세요! <a href='/oauth2/authorization/google'>Google로 로그인</a>";
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email"),
                "picture", principal.getAttribute("picture")
            );
        }
        return Map.of("error", "로그인이 필요합니다");
    }

    @GetMapping("/login")
    public String login() {
        return "로그인 페이지입니다. <a href='/oauth2/authorization/google'>Google로 로그인</a>";
    }
}
