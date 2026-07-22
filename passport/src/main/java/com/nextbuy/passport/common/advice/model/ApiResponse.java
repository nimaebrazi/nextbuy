package com.nextbuy.passport.common.advice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private List<ValidationError> validationErrors;
    private String timestamp;
    private String path;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toString();
    }

    public ApiResponse(boolean success, String message) {
        this(success, message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>(true, "Operation completed successfully", data);
        response.setErrorCode(null);
        response.setValidationErrors(null);
        return response;
    }
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        response.setErrorCode(null);
        response.setValidationErrors(null);
        return response;
    }
    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static ApiResponse<Void> failure(String errorCode, String message) {
        return failure(errorCode, message, null);
    }
    public static ApiResponse<Void> failure(
            String errorCode,
            String message,
            List<ValidationError> validationErrors) {
        ApiResponse<Void> response = new ApiResponse<>(false, message, null);
        response.setErrorCode(errorCode);
        response.setValidationErrors(validationErrors);
        return response;
    }
    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }


    @Getter
    @Setter
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;

        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }
    }
}
