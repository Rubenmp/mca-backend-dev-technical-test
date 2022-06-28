package com.mca.yourapp.conf;


import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Check tutorial <a href="https://www.baeldung.com/spring-boot-redis-cache">...</a>
 */
@Configuration
@EnableCaching
public class CacheConfig {
    public static final String GET_PRODUCTS_IN_PARALLEL_CACHE = "getProducts";
    public static final String GET_SIMILAR_PRODUCT_IDS_CACHE = "similarProductIds";
    public static final String GET_PRODUCT_CACHE = "getProduct";

    /**
     * Default configuration for Redis in-memory cache
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        final RedisSerializationContext.SerializationPair<Object> serializationStrategy =
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(serializationStrategy);
    }

    /**
     * Increased granularity for each Redis cache, specifying ttl (time-to-live)
     * */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(GET_PRODUCT_CACHE, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(GET_SIMILAR_PRODUCT_IDS_CACHE, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration(GET_PRODUCTS_IN_PARALLEL_CACHE, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
    }
}
