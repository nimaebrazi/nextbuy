package com.nextbuy.adhub.support.location.fixtures;

import com.nextbuy.adhub.location.domain.model.*;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.util.UUID;
import java.util.function.Consumer;

import static com.nextbuy.adhub.support.shared.Fakers.faker;

public final class Countries {

    public static final UUID TEST_COUNTRY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private Countries() {
    }

    public static Country random() {
        return Country.create(
                faker().country().name(),
                faker().regexify("[A-Z]{2}"),
                Geos.randomSlug(),
                Geos.randomPoint(),
                null
        );
    }

    public static Country withId(UUID id) {
        Country country = random();
        return Country.reconstruct(
                CountryId.of(id),
                country.getName(),
                country.getIsoCode(),
                country.getSlug(),
                country.getCentroid(),
                country.getBoundary(),
                country.getBbox()
        );
    }

    public static Country withFields(String name, String isoCode, String slug) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
                .set(Select.field(CreateParams::isoCode), isoCode)
                .set(Select.field(CreateParams::slug), slug)
        );
    }

    public static Country withIsoCode(String isoCode) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::isoCode), isoCode)
        );
    }

    public static Country withName(String name) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
        );
    }

    public static Country withSlug(String slug) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::slug), slug)
        );
    }

    public static Country iran() {
        return Country.create("Iran", "IR", "iran", GeoPoint.of(35.0, 51.0), null);
    }

    public static Country reconstructed(UUID id, BoundingBox bbox) {
        Country country = random();
        return Country.reconstruct(
                CountryId.of(id),
                country.getName(),
                country.getIsoCode(),
                country.getSlug(),
                country.getCentroid(),
                country.getBoundary(),
                bbox
        );
    }

    public static Country reconstructed(UUID id, String name, String isoCode, String slug,
                                        GeoPoint centroid, GeoBoundary boundary) {
        return Country.reconstruct(
                CountryId.of(id),
                name,
                isoCode,
                slug,
                centroid,
                boundary,
                null
        );
    }

    private static Country customize(Consumer<InstancioApi<CreateParams>> customizer) {
        var params = customizeParams(customizer);
        return paramsToCountry(params);
    }

    private static CreateParams customizeParams(Consumer<InstancioApi<CreateParams>> customizer) {
        var spec = Instancio.of(CreateParams.class)
                .supply(Select.field(CreateParams::name), () -> faker().country().name())
                .supply(Select.field(CreateParams::isoCode), () -> faker().regexify("[A-Z]{2}"))
                .supply(Select.field(CreateParams::slug), Geos::randomSlug)
                .supply(Select.field(CreateParams::centroid), Geos::randomPoint)
                .supply(Select.field(CreateParams::boundary), () -> null);
        customizer.accept(spec);
        return spec.create();
    }

    private static Country paramsToCountry(CreateParams p) {
        return Country.create(
                p.name(),
                p.isoCode(),
                p.slug(),
                p.centroid(),
                p.boundary()
        );
    }

    public record CreateParams(
            String name,
            String isoCode,
            String slug,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
    }

}
