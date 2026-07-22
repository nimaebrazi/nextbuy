package com.nextbuy.adhub.shared.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Mony(BigDecimal amount, String currency) {

    public Mony {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price amount must be positive");
        }

        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO code");
        }

        currency = currency.toUpperCase();
    }

    /**
     * Scale-insensitive: 10.0 EUR equals 10.00 EUR. hashCode strips trailing
     * zeros to stay consistent with compareTo-based equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mony price)) return false;
        return amount.compareTo(price.amount) == 0 && currency.equals(price.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }
}
