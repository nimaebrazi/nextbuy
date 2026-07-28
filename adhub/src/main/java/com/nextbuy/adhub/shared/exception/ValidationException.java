package com.nextbuy.adhub.shared.exception;

import java.util.Map;

public class ValidationException extends DomainException {

    private final Map<String, String> validationErrors;

    protected ValidationException(String message, Map<String, String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    protected ValidationException(String message, String messageKey, Map<String, String> validationErrors,
                                  Object... args) {
        super(message, messageKey, args);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> validationErrors() {
        return validationErrors;
    }

    public static class InvalidCategoryReference extends ValidationException {
        public InvalidCategoryReference() {
            super(
                    "Invalid category reference",
                    "validation.invalid_category",
                    Map.of("categoryId", "validation.category_not_found")
            );
        }
    }

    public static class InvalidLocationReference extends ValidationException {
        public InvalidLocationReference(String field, String reason) {
            this(field, reason, reason);
        }

        public InvalidLocationReference(String field, String reason, String reasonMessageKey) {
            super(
                    "Invalid location reference",
                    "validation.invalid_location",
                    Map.of(field, reasonMessageKey != null ? reasonMessageKey : reason)
            );
        }
    }

    public static class AdOwnershipMismatch extends ValidationException {
        public AdOwnershipMismatch() {
            super(
                    "Ad ownership mismatch",
                    "validation.ad_ownership_mismatch",
                    Map.of("ownerId", "validation.owner_mismatch")
            );
        }
    }
}
