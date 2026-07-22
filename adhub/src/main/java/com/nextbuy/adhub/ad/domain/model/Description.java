package com.nextbuy.adhub.ad.domain.model;

import com.nextbuy.adhub.ad.domain.exception.AdDomainException;

public record Description(String value) {
    private static final int DESCRIPTION_MAX_LENGTH = 5000;

    public Description {
        if (value != null && value.length() > DESCRIPTION_MAX_LENGTH) {
            throw new AdDomainException.ExceededLimitLength("description", String.valueOf(DESCRIPTION_MAX_LENGTH));
        }
    }

    public String value() {
        return value;
    }
}
