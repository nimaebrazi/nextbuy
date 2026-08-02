package com.nextbuy.passport.configuration;


import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Bucket4jConfiguration {

    private final DataRedisConnectionDetails redisConnectionDetails;
    @Value("${bucket4j.redis.database:1}")
    private int database;

    @Bean
    public RedisClient rateLimitRedisClient() {
        var standalone = redisConnectionDetails.getStandalone();
        RedisURI redisUri = RedisURI.builder()
                .withHost(standalone.getHost())
                .withPort(standalone.getPort())
                .withDatabase(database)
                .build();
        return RedisClient.create(redisUri);
    }

    @Bean
    public ProxyManager<byte[]> proxyManager(RedisClient redisClient) {
        StatefulRedisConnection<byte[], byte[]> connection = redisClient.connect(ByteArrayCodec.INSTANCE);

        return LettuceBasedProxyManager
                .builderFor(connection)
                .build();
    }
}
