package com.nextbuy.adhub.location.domain.exception;

import com.nextbuy.adhub.shared.exception.DomainException;

public class LocationDomainException extends DomainException {
    protected LocationDomainException(String message) {
        super(message);
    }

    public static class FieldIsRequired extends LocationDomainException {
        public FieldIsRequired(String model, String field) {
            super("%s '%s' field  is required!".formatted(model, field));
        }
    }

    public static class CountryMustBeIsoCode extends LocationDomainException {
        public CountryMustBeIsoCode() {
            super("Country iso code must be a 2-letter ISO code");
        }
    }

    public static class InvalidSlug extends LocationDomainException {
        public InvalidSlug(String message) {
            super(message);
        }
    }
}
