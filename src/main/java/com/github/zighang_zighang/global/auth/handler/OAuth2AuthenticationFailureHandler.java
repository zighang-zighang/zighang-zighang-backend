package com.github.zighang_zighang.global.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws ServletException, IOException {

        // 상세 예외 정보는 서버 로그에만 기록 (보안 강화)
        log.error("OAuth2 로그인 실패 - 예외 타입: {}, 메시지: {}", 
                exception.getClass().getSimpleName(), 
                exception.getMessage());
        
        // 스택 트레이스는 디버그 레벨에서만 로깅
        if (log.isDebugEnabled()) {
            log.debug("OAuth2 로그인 실패 상세 정보", exception);
        }
        
        // 프론트엔드에는 일반화된 에러 코드만 전달 (보안상 안전)
        String errorRedirectUrl = String.format(
            "%s/auth/error#code=%s",
            frontendBaseUrl,
            "OAUTH2_FAILURE"
        );
        
        getRedirectStrategy().sendRedirect(request, response, errorRedirectUrl);
    }
}
