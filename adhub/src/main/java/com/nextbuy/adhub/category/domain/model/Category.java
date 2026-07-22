package com.nextbuy.adhub.category.domain.model;


import com.nextbuy.adhub.category.domain.exception.CategoryDomainException;

public class Category {
    private final CategoryId id;
    private final String slug;
    private String name;
    private CategoryId parentId; // nullable

    private Category(CategoryId id, String slug, String name, CategoryId parentId) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.parentId = parentId;
    }

    public static Category create(
            String name,
            String slug,
            CategoryId parentId
    ) {
        String trimmedName = name == null ? null : name.trim();
        String trimmedSlug = slug == null ? null : slug.trim();

        var category = new Category(
                CategoryId.unassigned(),
                trimmedSlug,
                trimmedName,
                parentId
        );

        category.validateCategory();
        return category;
    }

    public static Category reconstruct(
            CategoryId id,
            String name,
            String slug,
            CategoryId parentId
    ) {
        return new Category(
                id,
                slug,
                name,
                parentId
        );
    }

    public void rename(String newName) {
        String trimmedName = newName == null ? null : newName.trim();
        validateName(trimmedName);
        this.name = trimmedName;
    }

    public void assignParent(CategoryId newParentId) {
        if (newParentId != null && newParentId.equals(this.id)) {
            throw new CategoryDomainException.CategoryCannotBeItsOwnParentException(newParentId);
        }
        this.parentId = newParentId;
    }

    private void validateCategory() {
        validateName(this.name);
        validateSlug(this.slug);
    }

    private void validateSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new CategoryDomainException.FieldIsRequired("slug");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new CategoryDomainException.FieldIsRequired("name");
        }
    }


    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public CategoryId getParentId() {
        return parentId;
    }
}
