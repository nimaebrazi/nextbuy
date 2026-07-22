package com.nextbuy.passport.common.advice.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {

    @Getter
    private final String errorCode;

    @Getter
    private final int status;

    protected BaseException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    protected BaseException(String message, String errorCode, int status, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }
}
