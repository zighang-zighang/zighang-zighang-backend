package com.github.zighang_zighang.global.exception;

import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.sentry.Sentry;
import io.sentry.protocol.Request;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ApiResponse<?> noResourceFoundException(Exception ignored) {

        return GlobalExceptionCode.NOT_FOUND.toResponse();
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ApiResponse<?> authorizationDeniedException(AuthorizationDeniedException ignored) {

        return GlobalExceptionCode.NOT_PERMITTED.toResponse();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<?> authenticationException(AuthenticationException e, HttpServletRequest request) {
        
        sentry(e, request);
        return AuthExceptionCode.OAUTH2_FAILURE.toResponse();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> httpMessageNotReadableException(HttpMessageNotReadableException ignored) {

        return GlobalExceptionCode.BODY_NOT_READABLE.toResponse();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        if (Objects.isNull(e.getBindingResult().getFieldError())) {

            return ApiResponse.error(GlobalExceptionCode.BODY_VALIDATION_FAILED.getCode(), e.getMessage());
        }

        String field = e.getBindingResult().getFieldError().getField();
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        String errorMessage = String.format("%s은(는) %s", field, message);

        return ApiResponse.error(GlobalExceptionCode.BODY_VALIDATION_FAILED.getCode(), errorMessage);
    }

    @ExceptionHandler(ApiException.class)
    public ApiResponse<?> apiException(ApiException e, HttpServletRequest request) {

        sentry(e, request);
        return e.toResponse();
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> exception(Exception e, HttpServletRequest request) {

        sentry(e, request);
        return GlobalExceptionCode.EXCEPTION.toResponse();
    }

    private void sentry(Throwable throwable, HttpServletRequest request) {

        Sentry.captureException(
                throwable, (scope) -> {
//                    TODO: 스프링 시큐리티 설정 후, 주석 해제
//                    if (request.getUserPrincipal() instanceof UserAuthentication authentication) {
//                        User user = authentication.getPrincipal();
//                        io.sentry.protocol.User sentryUser = new io.sentry.protocol.User();
//
//                        sentryUser.setId(user.getId());
//                        sentryUser.setEmail(user.getEmail());
//                        sentryUser.setName(user.getName());
//                        sentryUser.setIpAddress(request.getRemoteAddr());
//
//                        scope.setUser(sentryUser);
//                    }

                    if (throwable instanceof ApiException ae) {
                        scope.setTag("error.code", ae.getErrorCode());
                    }

                    Request sentryRequest = scope.getRequest();

                    if (Objects.isNull(sentryRequest)) {
                        sentryRequest = new Request();
                    }

                    if (request instanceof ContentCachingRequestWrapper wrapper) {
                        byte[] content = wrapper.getContentAsByteArray();

                        if (content.length > 0) {
                            String body = new String(content, StandardCharsets.UTF_8);

                            sentryRequest.setBodySize((long) content.length);
                            sentryRequest.setData(body);
                        }

                        scope.setRequest(sentryRequest);
                    }
                }
        );
    }
}