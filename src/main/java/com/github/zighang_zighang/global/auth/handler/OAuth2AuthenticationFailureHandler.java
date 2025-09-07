package com.github.zighang_zighang.global.auth.handler;

import com.github.zighang_zighang.global.auth.util.RedirectValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final RedirectValidator redirectValidator;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws ServletException, IOException {
        log.error("OAuth2 로그인 실패", exception);

        String targetBaseUrl = resolveRedirectOnFailure(request);

        // 프론트엔드에는 일반화된 에러 코드만 전달 (보안상 안전)
        String errorRedirectUrl = String.format(
            "%s/auth/error#code=%s",
                targetBaseUrl,
            "OAUTH2_FAILURE"
        );
        
        getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
    }

    private String resolveRedirectOnFailure(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("redirect_uri".equals(cookie.getName())) {
                    String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    if (redirectValidator.isAuthorized(decoded)) {
                        return decoded;
                    }
                }
            }
        }
        return frontendBaseUrl;
    }
}
