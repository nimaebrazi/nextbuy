package com.nextbuy.adhub.architecture.ad;

import com.nextbuy.adhub.AdhubApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("architecture:ad:AdModuleTest")
class AdModuleTest {

    @Test
    @DisplayName("ad module is registered")
    void adModuleIsRegistered() {
        var modules = ApplicationModules.of(AdhubApplication.class);
        assertThat(modules.getModuleByName("ad")).isPresent();
    }
}
