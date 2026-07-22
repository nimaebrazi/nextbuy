package com.nextbuy.adhub.support.location.fixtures;

import com.nextbuy.adhub.location.domain.model.*;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.util.UUID;
import java.util.function.Consumer;

import static com.nextbuy.adhub.support.shared.Fakers.faker;

public final class Provinces {

    public static final UUID TEST_PROVINCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID TEST_COUNTRY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private Provinces() {
    }

    public static Province random() {
        return randomInCountry(CountryId.of(UUID.randomUUID()));
    }

    public static Province randomInCountry(CountryId countryId) {
        return Province.create(
                faker().address().state(),
                Geos.randomSlug(),
                countryId,
                Geos.randomPoint(),
                null
        );
    }

    public static Province withId(UUID id) {
        Province province = random();
        return Province.reconstruct(
                ProvinceId.of(id),
                province.getName(),
                province.getSlug(),
                province.getCountryId(),
                province.getCentroid(),
                province.getBoundary(),
                province.getBbox()
        );
    }

    public static Province withNameAndSlug(String name, String slug) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
                .set(Select.field(CreateParams::slug), slug)
        );
    }

    public static Province withName(String name) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
        );
    }

    public static Province withoutCountry() {
        return customize(spec -> spec
                .set(Select.field(CreateParams::countryId), null)
        );
    }

    public static Province withCountry(CountryId countryId) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::countryId), countryId)
        );
    }

    public static Province reconstructed(UUID id, BoundingBox bbox) {
        Province province = random();
        return Province.reconstruct(
                ProvinceId.of(id),
                province.getName(),
                province.getSlug(),
                province.getCountryId(),
                province.getCentroid(),
                province.getBoundary(),
                bbox
        );
    }

    private static Province customize(Consumer<InstancioApi<CreateParams>> customizer) {
        var params = customizeParams(customizer);
        return paramsToProvince(params);
    }

    private static CreateParams customizeParams(Consumer<InstancioApi<CreateParams>> customizer) {
        var spec = Instancio.of(CreateParams.class)
                .supply(Select.field(CreateParams::name), () -> faker().address().state())
                .supply(Select.field(CreateParams::slug), Geos::randomSlug)
                .supply(Select.field(CreateParams::countryId), () -> CountryId.of(UUID.randomUUID()))
                .supply(Select.field(CreateParams::centroid), Geos::randomPoint)
                .supply(Select.field(CreateParams::boundary), () -> null);
        customizer.accept(spec);
        return spec.create();
    }

    private static Province paramsToProvince(CreateParams p) {
        return Province.create(
                p.name(),
                p.slug(),
                p.countryId(),
                p.centroid(),
                p.boundary()
        );
    }

    public record CreateParams(
            String name,
            String slug,
            CountryId countryId,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
    }

}
