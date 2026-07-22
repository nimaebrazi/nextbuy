package com.nextbuy.adhub.support.location.fixtures;

import com.nextbuy.adhub.location.domain.model.*;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.util.UUID;
import java.util.function.Consumer;

import static com.nextbuy.adhub.support.shared.Fakers.faker;

public final class Cities {

    public static final UUID TEST_CITY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private Cities() {
    }

    public static City random() {
        return randomInProvince(ProvinceId.of(UUID.randomUUID()));
    }

    public static City randomInProvince(ProvinceId provinceId) {
        return City.create(
                faker().address().city(),
                Geos.randomSlug(),
                provinceId,
                Geos.randomPoint(),
                null
        );
    }

    public static City withId(UUID id) {
        City city = random();
        return City.reconstruct(
                CityId.of(id),
                city.getName(),
                city.getSlug(),
                city.getProvinceId(),
                city.getCentroid(),
                city.getBoundary(),
                city.getBbox()
        );
    }

    public static City withIdAndSlug(UUID id, String slug) {
        City city = random();
        return City.reconstruct(
                CityId.of(id),
                city.getName(),
                slug,
                city.getProvinceId(),
                city.getCentroid(),
                city.getBoundary(),
                city.getBbox()
        );
    }

    public static City withNameAndSlug(String name, String slug) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
                .set(Select.field(CreateParams::slug), slug)
        );
    }

    public static City withName(String name) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
        );
    }

    public static City withoutProvince() {
        return customize(spec -> spec
                .set(Select.field(CreateParams::provinceId), null)
        );
    }

    public static City withProvince(ProvinceId provinceId) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::provinceId), provinceId)
        );
    }

    public static City reconstructed(UUID id, BoundingBox bbox) {
        City city = random();
        return City.reconstruct(
                CityId.of(id),
                city.getName(),
                city.getSlug(),
                city.getProvinceId(),
                city.getCentroid(),
                city.getBoundary(),
                bbox
        );
    }

    public static City tehran() {
        return City.reconstruct(
                CityId.of(UUID.randomUUID()),
                "Tehran",
                "tehran",
                ProvinceId.of(UUID.randomUUID()),
                GeoPoint.of(35.6892, 51.3890),
                null,
                BoundingBox.of(50.9719467, 35.5821, 51.6347084, 35.8002701)
        );
    }

    private static City customize(Consumer<InstancioApi<CreateParams>> customizer) {
        var params = customizeParams(customizer);
        return paramsToCity(params);
    }

    private static CreateParams customizeParams(Consumer<InstancioApi<CreateParams>> customizer) {
        var spec = Instancio.of(CreateParams.class)
                .supply(Select.field(CreateParams::name), () -> faker().address().city())
                .supply(Select.field(CreateParams::slug), Geos::randomSlug)
                .supply(Select.field(CreateParams::provinceId), () -> ProvinceId.of(UUID.randomUUID()))
                .supply(Select.field(CreateParams::centroid), Geos::randomPoint)
                .supply(Select.field(CreateParams::boundary), () -> null);
        customizer.accept(spec);
        return spec.create();
    }

    private static City paramsToCity(CreateParams p) {
        return City.create(
                p.name(),
                p.slug(),
                p.provinceId(),
                p.centroid(),
                p.boundary()
        );
    }

    public record CreateParams(
            String name,
            String slug,
            ProvinceId provinceId,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
    }

}
