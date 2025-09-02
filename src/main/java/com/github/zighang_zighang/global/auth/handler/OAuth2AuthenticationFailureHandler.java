package com.github.zighang_zighang.global.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws ServletException, IOException {

        log.error("OAuth2 로그인 실패: {}", exception.getMessage());
        exception.printStackTrace();
        
        // 프론트엔드 에러 페이지로 리다이렉트
        String errorRedirectUrl = String.format(
            "https://zighang-zighang-frontend.vercel.app/auth/error#message=%s&details=%s",
            java.net.URLEncoder.encode(AuthExceptionCode.OAUTH2_FAILURE.getMessage(), java.nio.charset.StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(exception.getClass().getSimpleName(), java.nio.charset.StandardCharsets.UTF_8)
        );
        
        getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
    }
}
