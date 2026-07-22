package com.nextbuy.adhub.category.infrastructure.api;

import com.nextbuy.adhub.category.api.CategoryExistenceChecker;
import com.nextbuy.adhub.category.domain.model.CategoryId;
import com.nextbuy.adhub.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryExistenceCheckerImpl implements CategoryExistenceChecker {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean exists(UUID categoryId) {
        return categoryRepository.existsById(CategoryId.of(categoryId));
    }
}
