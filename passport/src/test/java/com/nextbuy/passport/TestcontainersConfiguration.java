package com.nextbuy.passport;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	private final static DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:16-alpine");
	private final static DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7-alpine");

	@Bean
	@ServiceConnection
	@SuppressWarnings("resource")
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer(POSTGRES_IMAGE).withReuse(false);
	}

	@Bean
	@ServiceConnection(name = "redis")
	@SuppressWarnings("resource")
	RedisContainer redisContainer() {
		return new RedisContainer(REDIS_IMAGE).withReuse(false);
	}

}
