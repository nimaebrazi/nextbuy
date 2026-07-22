package com.nextbuy.adhub.bootstrap.advice.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(String resource) {
        super(resource + " not found", "ENTITY_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    public NotFoundException(String resource, String errorCode) {
        super(resource + " not found", errorCode, HttpStatus.NOT_FOUND.value());
    }
}
