package com.nextbuy.adhub.support.category.fixtures;

import com.nextbuy.adhub.category.domain.model.Category;
import com.nextbuy.adhub.category.domain.model.CategoryId;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;

import java.util.UUID;
import java.util.function.Consumer;

import static com.nextbuy.adhub.support.shared.Fakers.faker;

public final class Categories {

    public static final UUID TEST_CATEGORY_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID TEST_PARENT_CATEGORY_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000010");
    public static final UUID TEST_OTHER_CATEGORY_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000099");

    private Categories() {}

    public static Category random() {
        return customize(spec -> {});
    }

    public static Category withName(String name) {
        return customize(spec ->
                spec.set(Select.field(CreateParams::name), name));
    }

    public static Category withSlug(String slug) {
        return customize(spec ->
                spec.set(Select.field(CreateParams::slug), slug));
    }

    public static Category withNameAndSlug(String name, String slug) {
        return customize(spec -> spec
                .set(Select.field(CreateParams::name), name)
                .set(Select.field(CreateParams::slug), slug));
    }

    public static Category withParent(CategoryId parentId) {
        return customize(spec ->
                spec.set(Select.field(CreateParams::parentId), parentId));
    }

    public static Category withoutParent() {
        return customize(spec ->
                spec.set(Select.field(CreateParams::parentId), null));
    }

    public static Category withId(UUID id) {
        Category category = random();
        return Category.reconstruct(
                CategoryId.of(id),
                category.getName(),
                category.getSlug(),
                category.getParentId()
        );
    }

    public static Category reconstructFromPersisted(
            UUID id,
            String name,
            String slug,
            CategoryId parentId
    ) {
        return Category.reconstruct(CategoryId.of(id), name, slug, parentId);
    }

    private static Category customize(Consumer<InstancioApi<CreateParams>> customizer) {
        var params = customizeParams(customizer);
        return paramsToCategory(params);
    }

    private static CreateParams customizeParams(Consumer<InstancioApi<CreateParams>> customizer) {
        var spec = Instancio.of(CreateParams.class)
                .supply(Select.field(CreateParams::name), () -> faker().commerce().department())
                .supply(Select.field(CreateParams::slug), () -> faker().internet().slug())
                .set(Select.field(CreateParams::parentId), null);
        customizer.accept(spec);
        return spec.create();
    }

    private static Category paramsToCategory(CreateParams params) {
        return Category.create(params.name(), params.slug(), params.parentId());
    }

    public record CreateParams(
            String name,
            String slug,
            CategoryId parentId
    ) {}
}
