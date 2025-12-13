package org.superhan.loadtestdemo.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager(
      RedisConnectionFactory connectionFactory,
      @Value("${app.cache.item-ttl-seconds:60}") long itemTtlSeconds
  ) {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(itemTtlSeconds))
        .disableCachingNullValues();
    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .build();
  }
}
