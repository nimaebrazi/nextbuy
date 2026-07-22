package com.nextbuy.adhub.category.api;

import java.util.UUID;

public interface CategoryExistenceChecker {

    boolean exists(UUID categoryId);
}
