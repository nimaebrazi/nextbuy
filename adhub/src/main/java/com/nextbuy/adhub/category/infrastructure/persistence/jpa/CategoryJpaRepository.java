package com.nextbuy.adhub.category.infrastructure.persistence.jpa;

import com.nextbuy.adhub.category.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
}
