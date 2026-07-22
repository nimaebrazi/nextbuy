package com.nextbuy.adhub.architecture.location;

import com.nextbuy.adhub.AdhubApplication;
import com.nextbuy.adhub.location.api.LocationHierarchyValidator;
import com.nextbuy.adhub.location.api.LocationSelection;
import com.nextbuy.adhub.location.api.ValidatedLocation;
import com.nextbuy.adhub.location.domain.model.Neighbourhood;
import com.nextbuy.adhub.location.infrastructure.api.LocationHierarchyValidatorImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("architecture:location:LocationModuleTest")
class LocationModuleTest {

    private static ApplicationModules modules;
    private static ApplicationModule location;

    @BeforeAll
    static void setUpModules() {
        modules = ApplicationModules.of(AdhubApplication.class);
        location = modules.getModuleByName("location").orElseThrow();
    }

    @Test
    @DisplayName("It should register the location module.")
    void should_RegisterLocationModule() {
        assertThat(modules.getModuleByName("location")).isPresent();
    }

    @Test
    @DisplayName("It should keep the location module closed.")
    void should_BeClosedModule() {
        assertThat(location.isOpen()).isFalse();
        assertThat(location.getDisplayName()).isEqualTo("Location");
    }

    @Test
    @DisplayName("It should expose the LocationApi named interface.")
    void should_ExposeLocationApiNamedInterface() {
        assertThat(location.getNamedInterfaces().getByName("LocationApi")).isPresent();
    }

    @Test
    @DisplayName("It should expose public API types and hide internals.")
    void should_ExposePublicApiTypes_AndHideInternals() {
        assertThat(location.isExposed(LocationHierarchyValidator.class)).isTrue();
        assertThat(location.isExposed(LocationSelection.class)).isTrue();
        assertThat(location.isExposed(ValidatedLocation.class)).isTrue();

        assertThat(location.isExposed(Neighbourhood.class)).isFalse();
        assertThat(location.isExposed(LocationHierarchyValidatorImpl.class)).isFalse();
    }

    @Test
    @DisplayName("It should allow only shared named interfaces as dependencies.")
    void should_AllowOnlySharedNamedInterfaces() {
        var allowedNamed = location.getAllowedDependencies(modules).stream()
                .filter(dep -> dep.getTargetNamedInterface().isNamed())
                .map(dep -> dep.getTargetModule().getIdentifier()
                        + "::" + dep.getTargetNamedInterface().getName())
                .toList();

        assertThat(allowedNamed).containsExactlyInAnyOrder(
                "shared::SharedDomain",
                "shared::SharedExceptions"
        );
    }
}
