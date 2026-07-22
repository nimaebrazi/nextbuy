package com.nextbuy.adhub.architecture;

import com.nextbuy.adhub.AdhubApplication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

@DisplayName("architecture:ModulithStructureTest")
class ModulithStructureTest {

    @Test
    @DisplayName("verifies modular structure")
    void verifyModularStructure() {
        ApplicationModules.of(AdhubApplication.class).verify();
    }

    @Test
    @DisplayName("prints module graph")
    void printModuleGraph() {
        ApplicationModules.of(AdhubApplication.class).forEach(System.out::println);
    }
}
