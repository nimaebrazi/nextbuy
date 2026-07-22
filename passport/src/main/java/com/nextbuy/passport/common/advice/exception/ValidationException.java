package com.nextbuy.passport.common.advice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ValidationException extends BaseException {
    private final Map<String, String> validationErrors;

    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message, "VALIDATION_ERROR", HttpStatus.UNPROCESSABLE_ENTITY.value());
        this.validationErrors = validationErrors;
    }
}
