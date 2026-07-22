package com.nextbuy.adhub.location.application.command.importdata;

public record ImportLocationHierarchyResult(
        int countriesCreated,
        int provincesCreated,
        int citiesCreated,
        int countriesSkipped,
        int provincesSkipped,
        int citiesSkipped
) {
}
