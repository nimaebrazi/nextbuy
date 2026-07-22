package com.nextbuy.adhub.ad.domain.model;

import com.nextbuy.adhub.ad.domain.exception.AdDomainException;

public record Title(String value) {
    private static final int TITLE_MAX_LENGTH = 200;

    public Title {
        if (value == null || value.isBlank()) {
            throw new AdDomainException.FieldIsRequired("title");
        }

        if (value.length() > TITLE_MAX_LENGTH) {
            throw new AdDomainException.ExceededLimitLength("title", String.valueOf(TITLE_MAX_LENGTH));
        }
    }

    public String value() {
        return value;
    }
}
