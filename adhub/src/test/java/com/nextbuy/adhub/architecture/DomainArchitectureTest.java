package com.nextbuy.adhub.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Checkstyle ImportControl (validate phase) enforces forbidden domain imports at source level;
// ArchUnit below only sees bytecode and cannot detect SOURCE-retention annotations like Lombok.
@DisplayName("architecture:DomainArchitectureTest")
class DomainArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("com.nextbuy.adhub");

    @Test
    @DisplayName("domain packages must not depend on Spring or JPA")
    void domainPackagesMustNotDependOnSpringOrJpa() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .and().haveNameNotMatching(".*package-info")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "lombok.."
                )
                .check(classes);
    }
}
