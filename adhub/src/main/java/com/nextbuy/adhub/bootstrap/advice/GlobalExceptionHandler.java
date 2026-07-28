package com.nextbuy.adhub.bootstrap.advice;

import com.nextbuy.adhub.bootstrap.advice.exception.BaseException;
import com.nextbuy.adhub.bootstrap.advice.model.ApiResponse;
import com.nextbuy.adhub.bootstrap.advice.model.ApiResponse.ValidationError;
import com.nextbuy.adhub.location.api.LocationValidationException;
import com.nextbuy.adhub.shared.exception.DomainException;
import com.nextbuy.adhub.shared.exception.ValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String ERROR_DEFAULT = "error.default";

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        resolveFieldMessage(error.getDefaultMessage()),
                        error.getRejectedValue()))
                .toList();

        log.warn("Validation failed: {}", validationErrors);
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
                msg("error.validation_failed"), validationErrors, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex, WebRequest request) {

        List<ValidationError> validationErrors = ex.validationErrors()
                .entrySet()
                .stream()
                .map(entry -> new ValidationError(
                        entry.getKey(),
                        resolveFieldMessage(entry.getValue()),
                        null))
                .toList();

        log.warn("Validation exception: {}", ex.getMessage());
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR",
                msgOrDefault(ex.getMessageKey(), ex.getArgs(), "error.validation_error"),
                validationErrors, request);
    }

    @ExceptionHandler(LocationValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleLocationValidationException(
            LocationValidationException ex, WebRequest request) {

        String fieldMessage = msgOrDefault(ex.getMessageKey(), ex.getArgs(), "error.validation_error");
        List<ValidationError> validationErrors = List.of(
                new ValidationError(ex.getField(), fieldMessage, null)
        );
        log.warn("Location validation exception: {}", ex.getMessage());
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR",
                msgOrDefault(ex.getMessageKey(), ex.getArgs(), "error.validation_error"),
                validationErrors, request);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(
            BaseException ex, WebRequest request) {

        log.warn("Application exception [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return error(resolveStatus(ex.getStatus(), HttpStatus.BAD_REQUEST),
                ex.getErrorCode(),
                msgOrDefault(ex.getMessageKey(), ex.getArgs(), "error.business"),
                request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {

        log.warn("Data integrity violation: {}", ex.getMessage());
        return error(HttpStatus.CONFLICT, "CONFLICT", msg("error.conflict"), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {

        return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                msg("error.method_not_allowed"), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<@NonNull ApiResponse<Void>> handleNoResourceFoundException(
            NoResourceFoundException ex, WebRequest request) {

        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", msg("error.not_found"), request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(
            DomainException ex, WebRequest request) {
        log.warn("Domain rule violated: {}", ex.getMessage());
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "DOMAIN_RULE_VIOLATION",
                msgOrDefault(ex.getMessageKey(), ex.getArgs(), "error.domain_rule_violation"),
                request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {

        log.warn("Entity not found: {}", ex.getMessage());
        return error(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND",
                msg("error.entity_not_found"), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());
        String detail = msg("error.type_mismatch.detail", ex.getName());
        return error(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", msg("error.type_mismatch"),
                List.of(new ValidationError(ex.getName(), detail, ex.getValue())), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {

        log.warn("Missing required parameter: {}", ex.getParameterName());
        String detail = msg("error.missing_parameter.detail");
        return error(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", msg("error.missing_parameter"),
                List.of(new ValidationError(ex.getParameterName(), detail, null)),
                request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> new ValidationError(
                        simplifyPath(v.getPropertyPath().toString()),
                        resolveFieldMessage(v.getMessage()),
                        v.getInvalidValue()))
                .toList();
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
                msg("error.validation_failed"), validationErrors, request);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestHeader(
            MissingRequestHeaderException ex, WebRequest request) {
        String detail = msg("error.missing_header.detail");
        return error(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", msg("error.missing_header"),
                List.of(new ValidationError(ex.getHeaderName(), detail, null)),
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        log.warn("Malformed JSON request: {}", ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, "MALFORMED_JSON",
                msg("error.malformed_json"), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllUncaughtException(
            Exception ex, WebRequest request) {

        log.error("Unhandled exception occurred", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                msg("error.internal_server_error"), request);
    }

    private String msg(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        String fallback = messageSource.getMessage(
                ERROR_DEFAULT, null, "Request could not be processed.", locale);
        return messageSource.getMessage(key, args, fallback, locale);
    }

    private String msgOrDefault(String messageKey, Object[] args, String defaultKey) {
        return msg(messageKey != null ? messageKey : defaultKey, args != null ? args : new Object[0]);
    }

    private String resolveFieldMessage(String messageOrKey) {
        if (messageOrKey == null || messageOrKey.isBlank()) {
            return msg(ERROR_DEFAULT);
        }
        if (looksLikeMessageKey(messageOrKey)) {
            return msg(messageOrKey);
        }
        return messageOrKey;
    }

    private static boolean looksLikeMessageKey(String value) {
        return value.indexOf(' ') < 0 && value.contains(".");
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

    private static String simplifyPath(String path) {
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }
}
