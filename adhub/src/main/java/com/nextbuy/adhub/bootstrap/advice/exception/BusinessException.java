package com.nextbuy.adhub.bootstrap.advice.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR", HttpStatus.BAD_REQUEST.value());
    }

    public BusinessException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST.value());
    }

    public BusinessException(String message, String errorCode, int status) {
        super(message, errorCode, status);
    }

    public BusinessException(String message, String errorCode, String messageKey, Object... args) {
        super(message, errorCode, HttpStatus.BAD_REQUEST.value(), messageKey, args);
    }

    public BusinessException(String message, String errorCode, int status, String messageKey, Object... args) {
        super(message, errorCode, status, messageKey, args);
    }
}
