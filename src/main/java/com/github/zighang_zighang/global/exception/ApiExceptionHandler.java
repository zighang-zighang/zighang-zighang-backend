package com.github.zighang_zighang.global.exception;

import com.github.zighang_zighang.domain.user.entity.User;
import com.github.zighang_zighang.global.auth.exception.AuthExceptionCode;
import com.github.zighang_zighang.global.auth.service.CustomUserDetails;
import com.github.zighang_zighang.global.infra.storage.exception.StorageException;
import com.github.zighang_zighang.global.response.ApiResponse;
import io.sentry.Sentry;
import io.sentry.protocol.Request;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestControllerAdvice
public class ApiExceptionHandler {

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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {

        sentry(e, request);
        return StorageException.FILE_SIZE_EXCEEDED.toResponse();
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
                    if (request.getUserPrincipal() instanceof UsernamePasswordAuthenticationToken authentication) {
                        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
                        io.sentry.protocol.User sentryUser = new io.sentry.protocol.User();

                        sentryUser.setId(user.getId().toString());
                        sentryUser.setEmail(user.getEmail());
                        sentryUser.setUsername(user.getName());
                        sentryUser.setIpAddress(request.getRemoteAddr());

                        scope.setUser(sentryUser);
                    }

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