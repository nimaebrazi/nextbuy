package com.nextbuy.adhub.location.api;

import lombok.Getter;

@Getter
public class LocationValidationException extends RuntimeException {

    private final String field;

    private final String reason;

    public LocationValidationException(String field, String reason) {
        super(reason);
        this.field = field;
        this.reason = reason;
    }

    public String reason() {
        return reason;
    }
}
