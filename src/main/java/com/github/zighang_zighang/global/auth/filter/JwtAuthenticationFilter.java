package com.github.zighang_zighang.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zighang_zighang.global.auth.util.JwtUtil;
import com.github.zighang_zighang.global.exception.ApiException;
import com.github.zighang_zighang.global.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // Access token만 처리 (Authorization: Bearer)
            // Refresh token은 /auth/refresh 엔드포인트에서만 처리
            String accessToken = extractAccessTokenFromRequest(request);
            if (StringUtils.hasText(accessToken) && jwtUtil.validateAccessToken(accessToken)) {
                String email = jwtUtil.getEmailFromToken(accessToken);
                
                if (StringUtils.hasText(email)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Access token 인증 성공: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
            handleException(response, e);
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            return StringUtils.hasText(token) ? token : null;
        }
        return null;
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        String code = "INTERNAL_ERROR";
        String message = "알 수 없는 서버 오류가 발생했습니다.";

        if (e instanceof ApiException apiEx) {
            code = apiEx.getErrorCode();
            message = apiEx.getMessage();
        }

        ApiResponse<?> apiResponse = ApiResponse.error(code, message);
        String content = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(content);
        response.getWriter().flush();
    }

}
