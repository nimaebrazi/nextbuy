package com.nextbuy.adhub.location.domain;

import java.util.Locale;
import java.util.regex.Pattern;

import com.nextbuy.adhub.location.domain.exception.LocationDomainException;

/**
 * URL-safe slug validation shared by all location hierarchy levels.
 */
public final class SlugValidator {

    private static final Pattern SLUG = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");
    private static final int MAX_LENGTH = 100;

    private SlugValidator() {
    }

    public static String validateAndNormalize(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new LocationDomainException.InvalidSlug("slug is required");
        }
        String normalized = slug.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > MAX_LENGTH) {
            throw new LocationDomainException.InvalidSlug(
                    "slug must not exceed " + MAX_LENGTH + " characters");
        }
        if (!SLUG.matcher(normalized).matches()) {
            throw new LocationDomainException.InvalidSlug(
                    "slug must contain only lowercase letters, digits and single hyphens");
        }
        return normalized;
    }
}
