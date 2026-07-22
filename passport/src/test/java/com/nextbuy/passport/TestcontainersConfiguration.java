package com.nextbuy.passport;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
        var dockerImage = DockerImageName.parse("postgres:16-alpine");
		return new PostgreSQLContainer<>(dockerImage).withReuse(false);
	}

	@Bean
	@ServiceConnection(name = "redis")
	GenericContainer<?> redisContainer() {
        var dockerImage = DockerImageName.parse("redis:7-alpine");
		return new RedisContainer(dockerImage).withReuse(false);
	}

}
