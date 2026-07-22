package com.nextbuy.passport.common.advice;

import com.nextbuy.passport.common.advice.exception.BaseException;
import com.nextbuy.passport.common.advice.exception.ValidationException;
import com.nextbuy.passport.common.advice.model.ApiResponse;
import com.nextbuy.passport.common.advice.model.ApiResponse.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .toList();

        log.warn("Validation failed: {}", validationErrors);
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
                "Validation failed for one or more fields", validationErrors, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex, WebRequest request) {

        List<ValidationError> validationErrors = ex.getValidationErrors()
                .entrySet()
                .stream()
                .map(entry -> new ValidationError(entry.getKey(), entry.getValue(), null))
                .toList();

        log.warn("Validation exception: {}", ex.getMessage());
        return error(resolveStatus(ex.getStatus(), HttpStatus.BAD_REQUEST),
                ex.getErrorCode(), ex.getMessage(), validationErrors, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {

        log.warn("Bad credentials: {}", ex.getMessage());
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                "Invalid email or password", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());
        return error(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED",
                "Authentication failed", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        log.warn("Access denied: {}", ex.getMessage());
        return error(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You don't have permission to access this resource", request);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(
            BaseException ex, WebRequest request) {

        log.warn("Application exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return error(resolveStatus(ex.getStatus(), HttpStatus.BAD_REQUEST),
                ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {

        log.warn("Data integrity violation: {}", ex.getMessage());
        return error(HttpStatus.CONFLICT, "CONFLICT",
                "Resource already exists or violates a constraint", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {

        return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "Method not allowed", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(
            NoResourceFoundException ex, WebRequest request) {

        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Resource not found", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String message = String.format("Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        log.warn("Type mismatch for parameter '{}': {}", ex.getName(), message);
        return error(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", "Parameter type mismatch",
                List.of(new ValidationError(ex.getName(), message, ex.getValue())), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {

        log.warn("Missing required parameter: {}", ex.getParameterName());
        return error(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "Required parameter is missing",
                List.of(new ValidationError(ex.getParameterName(), "Required parameter is missing", null)),
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        log.warn("Malformed JSON request: {}", ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "Malformed JSON request", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllUncaughtException(
            Exception ex, WebRequest request) {

        log.error("Unhandled exception occurred", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred", request);
    }

    private ResponseEntity<ApiResponse<Void>> error(
            HttpStatus status,
            String errorCode,
            String message,
            WebRequest request) {

        return ResponseEntity.status(status)
                .body(ApiResponse.failure(errorCode, message).withPath(getRequestPath(request)));
    }

    private ResponseEntity<ApiResponse<Void>> error(
            HttpStatus status,
            String errorCode,
            String message,
            List<ValidationError> validationErrors,
            WebRequest request) {

        return ResponseEntity.status(status)
                .body(ApiResponse.failure(errorCode, message, validationErrors)
                        .withPath(getRequestPath(request)));
    }

    private HttpStatus resolveStatus(int statusCode, HttpStatus fallback) {
        HttpStatus resolved = HttpStatus.resolve(statusCode);
        return resolved != null ? resolved : fallback;
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            HttpServletRequest servletRequest = servletWebRequest.getRequest();
            return servletRequest.getRequestURI();
        }
        return "Unknown";
    }
}
