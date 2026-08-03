package com.nextbuy.adhub.location.domain.exception;

import com.nextbuy.adhub.shared.exception.DomainException;

public class LocationDomainException extends DomainException {
    protected LocationDomainException(String message) {
        super(message);
    }

    protected LocationDomainException(String message, String messageKey, Object... args) {
        super(message, messageKey, args);
    }

    public static class FieldIsRequired extends LocationDomainException {
        public FieldIsRequired(String model, String field) {
            super(
                    "%s '%s' field  is required!".formatted(model, field),
                    "location.error.field_required",
                    model,
                    field
            );
        }
    }

    public static class CountryMustBeIsoCode extends LocationDomainException {
        public CountryMustBeIsoCode() {
            super(
                    "Country iso code must be a 2-letter ISO code",
                    "location.error.country_iso"
            );
        }
    }

    public static class InvalidSlug extends LocationDomainException {
        public InvalidSlug(String message) {
            super(message, "location.error.invalid_slug");
        }
    }
}
