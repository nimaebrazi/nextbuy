package com.nextbuy.adhub.support.ad.fixtures;

import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.model.AdLocation;
import com.nextbuy.adhub.ad.domain.model.CategoryId;
import com.nextbuy.adhub.ad.domain.model.OwnerId;
import com.nextbuy.adhub.shared.domain.Mony;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

import static com.nextbuy.adhub.support.shared.Fakers.faker;

public final class Ads {

    public static final UUID TEST_AD_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");
    public static final UUID TEST_AD_ID_2 = UUID.fromString("00000000-0000-0000-0000-000000000064");
    public static final Instant NOW = Instant.parse("2026-07-11T12:00:00Z");

    private Ads() {
    }

    public static Ad draftForDb() {
        return customize(spec -> spec
                .set(Select.field(CreateParams::categoryId),
                        CategoryId.of(UUID.fromString("11111111-1111-1111-1111-111111111111")))
                .set(Select.field(CreateParams::location), AdLocations.fixtureIds()));
    }

    public static Ad withTitle(String title) {
        var params = customizeParams(spec -> spec
                .set(Select.field(CreateParams::title), title));

        return paramsToAd(params);
    }

    public static Ad withDetails(String title, String description) {
        var params = customizeParams(spec -> spec
                .set(Select.field(CreateParams::title), title)
                .set(Select.field(CreateParams::description), description));

        return paramsToAd(params);
    }

    public static Ad withNullLocation() {
        return customize(spec ->
                spec.set(Select.field(CreateParams::location), null)
        );
    }

    public static Ad withUnassignedCategoryId() {
        return customize(spec ->
                spec.set(Select.field(CreateParams::categoryId), null)
        );
    }

    public static Ad withNullPrice() {
        return customize(spec ->
                spec.set(Select.field(CreateParams::price), null)
        );
    }

    public static Ad randomDraft() {
        return paramsToAd(randomCreateParams());
    }

    public static Ad withPersistedId(UUID id) {
        return withId(id);
    }

    public static Ad withPersistedId() {
        return withPersistedId(TEST_AD_ID);
    }

    public static Ad withoutOwnerId() {
        return customize(spec -> spec
                .set(Select.field(CreateParams::ownerId), null)
        );
    }

    public static Ad withId() {
        Ad ad = randomDraft();
        ad.assignId(randomAdId());
        return ad;
    }

    public static Ad withId(UUID id) {
        Ad ad = randomDraft();
        ad.assignId(AdId.of(id));
        return ad;
    }

    public static Ad pendingModeration() {
        Ad ad = withPersistedId();
        ad.submitForModeration(NOW);
        ad.pullDomainEvents();
        return ad;
    }

    public static Ad active() {
        Ad ad = pendingModeration();
        ad.approve(NOW);
        ad.pullDomainEvents();
        return ad;
    }

    public static Ad rejected(String reason) {
        Ad ad = pendingModeration();
        ad.reject(reason, NOW);
        ad.pullDomainEvents();
        return ad;
    }

    public static Ad suspended(String reason) {
        Ad ad = active();
        ad.suspend(reason, NOW);
        ad.pullDomainEvents();
        return ad;
    }

    public static Ad expired() {
        Ad ad = active();
        ad.expire(NOW);
        ad.pullDomainEvents();
        return ad;
    }

    private static CreateParams randomCreateParams() {
        return customizeParams(spec -> {
        });
    }

    private static Ad customize(Consumer<InstancioApi<CreateParams>> customizer) {
        var params = customizeParams(customizer);
        return paramsToAd(params);
    }

    private static CreateParams customizeParams(Consumer<InstancioApi<CreateParams>> customizer) {
        var spec = Instancio.of(CreateParams.class)
                .supply(Select.field(CreateParams::ownerId), () -> OwnerId.of(randomLongId()))
                .supply(Select.field(CreateParams::title), () -> faker().commerce().productName())
                .supply(Select.field(CreateParams::description), () -> faker().lorem().paragraph())
                .supply(Select.field(CreateParams::price), Ads::randomPrice)
                .supply(Select.field(CreateParams::categoryId), () -> CategoryId.of(UUID.randomUUID()))
                .supply(Select.field(CreateParams::location), AdLocations::random);
        customizer.accept(spec);
        return spec.create();
    }

    public static Mony randomPrice() {
        BigDecimal amount = BigDecimal.valueOf(
                faker().number().randomDouble(2, 1, 50_000)
        ).setScale(2, RoundingMode.HALF_UP);
        return new Mony(amount, faker().currency().code());
    }

    public static AdId randomAdId() {
        return AdId.of(UUID.randomUUID());
    }

    private static long randomLongId() {
        return faker().number().numberBetween(1L, Long.MAX_VALUE);
    }

    public record CreateParams(
            OwnerId ownerId,
            String title,
            String description,
            Mony price,
            CategoryId categoryId,
            AdLocation location
    ) {
    }

    private static Ad paramsToAd(CreateParams p) {
        return Ad.createDraft(
                p.ownerId(),
                p.categoryId(),
                p.title(),
                p.description(),
                p.price(),
                p.location(),
                NOW
        );
    }
}
