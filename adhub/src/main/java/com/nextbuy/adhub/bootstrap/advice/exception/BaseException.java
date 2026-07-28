package com.nextbuy.adhub.bootstrap.advice.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final String errorCode;
    private final int status;
    private final String messageKey;
    private final Object[] args;

    protected BaseException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.messageKey = null;
        this.args = new Object[0];
    }

    protected BaseException(String message, String errorCode, int status, String messageKey, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.messageKey = messageKey;
        this.args = args != null ? args : new Object[0];
    }

    protected BaseException(String message, String errorCode, int status, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
        this.messageKey = null;
        this.args = new Object[0];
    }

    protected BaseException(String message, String errorCode, int status, Throwable cause,
                            String messageKey, Object... args) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
        this.messageKey = messageKey;
        this.args = args != null ? args : new Object[0];
    }
}
