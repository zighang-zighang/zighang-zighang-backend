package com.github.zighang_zighang.global.exception;

import com.github.zighang_zighang.global.response.ApiResponse;
import io.sentry.Sentry;
import io.sentry.protocol.Request;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ApiResponse<?> noResourceFoundException(Exception ignored) {

        return GlobalExceptionCode.NOT_FOUND.toResponse();
    }

//    @ExceptionHandler(AuthorizationDeniedException.class)
//    public ApiResponse<?> authorizationDeniedException(AuthorizationDeniedException ignored) {
//
//        return GlobalExceptionCode.NOT_PERMITTED.toResponse();
//    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> httpMessageNotReadableException(HttpMessageNotReadableException ignored) {

        return GlobalExceptionCode.BODY_NOT_READABLE.toResponse();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<?> constraintViolationException(ConstraintViolationException e) {

        ConstraintViolation<?> violation = e.getConstraintViolations().iterator().next();

        String field = violation.getPropertyPath().toString();
        if (field.contains(".")) {
            field = field.substring(field.lastIndexOf(".") + 1);
        }

        String errorMessage = String.format("%s은(는) %s.", field, violation.getMessage());

        return ApiResponse.error(GlobalExceptionCode.BODY_VALIDATION_FAILED.getCode(), errorMessage);
    }

    @ExceptionHandler(ApiException.class)
    public ApiResponse<?> apiException(ApiException e, HttpServletRequest request) {

        sentry(e, request);
        return e.getCode().toResponse();
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> exception(Exception e, HttpServletRequest request) {

        log.error("", e);

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
                        scope.setTag("error.code", ae.getCode().getCode());
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