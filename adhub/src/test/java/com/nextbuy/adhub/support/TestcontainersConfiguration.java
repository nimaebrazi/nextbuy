package com.nextbuy.adhub.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        var image = DockerImageName.parse("postgis/postgis:16-3.4")
                .asCompatibleSubstituteFor("postgres");
        return new PostgreSQLContainer(image).withReuse(false);
    }
}
