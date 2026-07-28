package com.nextbuy.adhub.shared.exception;

public abstract class DomainException extends RuntimeException {

    private final String messageKey;
    private final Object[] args;

    protected DomainException(String message) {
        this(message, null);
    }

    protected DomainException(String message, String messageKey, Object... args) {
        super(message);
        this.messageKey = messageKey;
        this.args = args != null ? args : new Object[0];
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
