package com.nextbuy.adhub.architecture.category;

import com.nextbuy.adhub.AdhubApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("architecture:category:CategoryModuleTest")
class CategoryModuleTest {

    @Test
    @DisplayName("category module is registered")
    void categoryModuleIsRegistered() {
        var modules = ApplicationModules.of(AdhubApplication.class);
        assertThat(modules.getModuleByName("category")).isPresent();
    }
}
