package com.nextbuy.adhub.category.domain.repository;

import java.util.Optional;

import com.nextbuy.adhub.category.domain.model.Category;
import com.nextbuy.adhub.category.domain.model.CategoryId;

public interface CategoryRepository {
    Category save(Category category);

    Optional<Category> findById(CategoryId id);

    boolean existsById(CategoryId id);
}