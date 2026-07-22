package com.nextbuy.adhub.shared.exception;

import java.util.Map;

public class ValidationException extends DomainException {

    private final Map<String, String> validationErrors;

    protected ValidationException(String message, Map<String, String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> validationErrors() {
        return validationErrors;
    }

    public static class InvalidCategoryReference extends ValidationException {
        public InvalidCategoryReference() {
            super("Invalid category reference", Map.of("categoryId", "Category does not exist"));
        }
    }

    public static class InvalidLocationReference extends ValidationException {
        public InvalidLocationReference(String field, String reason) {
            super("Invalid location reference", Map.of(field, reason));
        }
    }

    public static class AdOwnershipMismatch extends ValidationException {
        public AdOwnershipMismatch() {
            super("Ad ownership mismatch", Map.of("ownerId", "Caller is not the owner of this ad"));
        }
    }
}
