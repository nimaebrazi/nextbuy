package com.nextbuy.adhub.category.infrastructure.persistence.repository;

import com.nextbuy.adhub.category.domain.model.Category;
import com.nextbuy.adhub.category.domain.model.CategoryId;
import com.nextbuy.adhub.category.domain.repository.CategoryRepository;
import com.nextbuy.adhub.category.infrastructure.persistence.jpa.CategoryJpaRepository;
import com.nextbuy.adhub.category.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryPersistenceMapper mapper;

    public CategoryRepositoryImpl(CategoryJpaRepository jpaRepository, CategoryPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Category save(Category category) {
        if (!category.getId().isAssigned()) {
            var entity = mapper.toEntity(category);
            var saved = jpaRepository.save(entity);
            return mapper.toDomain(saved);
        }

        var entity = jpaRepository.findById(category.getId().valueOrThrow())
                .orElseThrow(() -> new IllegalStateException("Category not found: " + category.getId()));
        mapper.updateEntity(category, entity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.valueOrThrow()).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(CategoryId id) {
        return jpaRepository.existsById(id.valueOrThrow());
    }
}
