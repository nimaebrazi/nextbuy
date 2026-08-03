package com.nextbuy.adhub.location.api;

import lombok.Getter;

@Getter
public class LocationValidationException extends RuntimeException {

    private final String field;
    private final String reason;
    private final String messageKey;
    private final Object[] args;

    public LocationValidationException(String field, String reason) {
        this(field, reason, null);
    }

    public LocationValidationException(String field, String reason, String messageKey, Object... args) {
        super(reason);
        this.field = field;
        this.reason = reason;
        this.messageKey = messageKey;
        this.args = args != null ? args : new Object[0];
    }

    public String reason() {
        return reason;
    }
}
