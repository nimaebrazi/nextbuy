package com.nextbuy.adhub.category.domain.exception;

import com.nextbuy.adhub.category.domain.model.CategoryId;
import com.nextbuy.adhub.shared.exception.DomainException;

public class CategoryDomainException extends DomainException {
    protected CategoryDomainException(String message) {
        super(message);
    }

    public static class CategoryCannotBeItsOwnParentException extends CategoryDomainException {
        public CategoryCannotBeItsOwnParentException(CategoryId categoryId) {
            super("A category cannot be its own parent with id %s.".formatted(categoryId.value()));
        }
    }

    public static class FieldIsRequired extends CategoryDomainException {
        public FieldIsRequired(String field) {
            super("Category '%s' field is required".formatted(field));
        }
    }
}
