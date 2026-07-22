package com.nextbuy.adhub.support.location.fixtures;

import com.nextbuy.adhub.location.domain.model.*;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.util.UUID;
import java.util.function.Consumer;

import static com.nextbuy.adhub.support.shared.Fakers.faker;

public final class Neighbourhoods {

    public static final UUID TEST_NEIGHBOURHOOD_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    public static final UUID TEST_CITY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private Neighbourhoods() {
    }

    public static Neighbourhood random() {
        return randomInCity(CityId.of(UUID.randomUUID()));
    }

    public static Neighbourhood randomInCity(CityId cityId) {
        return Neighbourhood.create(
                faker().address().streetName(),
                Geos.randomSlug(),
                cityId,
                Geos.randomPoint(),
                null
        );
    }

    public static Neighbourhood withId(UUID id) {
        return withIdInCity(id, CityId.of(UUID.randomUUID()));
    }

    public static Neighbourhood withIdInCity(UUID id, CityId cityId) {
        Neighbourhood neighbourhood = randomInCity(cityId);
        return Neighbourhood.reconstruct(
                NeighbourhoodId.of(id),
                neighbourhood.getName(),
                neighbourhood.getSlug(),
                cityId,
                neighbourhood.getCentroid(),
                neighbourhood.getBoundary(),
                neighbourhood.getBbox()
        );
    }

    public static Neighbourhood withIdCityAndBoundary(UUID id, CityId cityId, GeoBoundary boundary) {
        Neighbourhood neighbourhood = randomInCity(cityId);
        return Neighbourhood.reconstruct(
                NeighbourhoodId.of(id),
                neighbourhood.getName(),
                neighbourhood.getSlug(),
                cityId,
                neighbourhood.getCentroid(),
                boundary,
                neighbourhood.getBbox()
        );
    }

    public static Neighbourhood withNameAndSlug(String name, String slug) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
                .set(Select.field(CreateParams::slug), slug)
        );
    }

    public static Neighbourhood withoutCity() {
        return customize(spec -> spec
                .set(Select.field(CreateParams::cityId), null)
        );
    }

    public static Neighbourhood withCity(CityId cityId) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::cityId), cityId)
        );
    }

    public static Neighbourhood withName(String name) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
        );
    }

    public static Neighbourhood reconstructed(UUID id, BoundingBox bbox) {
        Neighbourhood neighbourhood = random();
        return Neighbourhood.reconstruct(
                NeighbourhoodId.of(id),
                neighbourhood.getName(),
                neighbourhood.getSlug(),
                neighbourhood.getCityId(),
                neighbourhood.getCentroid(),
                neighbourhood.getBoundary(),
                bbox
        );
    }

    private static Neighbourhood customize(Consumer<InstancioApi<CreateParams>> customizer) {
        var params = customizeParams(customizer);
        return paramsToNeighbourhood(params);
    }

    private static CreateParams customizeParams(Consumer<InstancioApi<CreateParams>> customizer) {
        var spec = Instancio.of(CreateParams.class)
                .supply(Select.field(CreateParams::name), () -> faker().address().streetName())
                .supply(Select.field(CreateParams::slug), Geos::randomSlug)
                .supply(Select.field(CreateParams::cityId), () -> CityId.of(UUID.randomUUID()))
                .supply(Select.field(CreateParams::centroid), Geos::randomPoint)
                .supply(Select.field(CreateParams::boundary), () -> null);
        customizer.accept(spec);
        return spec.create();
    }

    private static Neighbourhood paramsToNeighbourhood(CreateParams p) {
        return Neighbourhood.create(
                p.name(),
                p.slug(),
                p.cityId(),
                p.centroid(),
                p.boundary()
        );
    }

    public record CreateParams(
            String name,
            String slug,
            CityId cityId,
            GeoPoint centroid,
            GeoBoundary boundary
    ) {
    }

}
