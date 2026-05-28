package com.example.couplebackend.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(@Value("${app.cache.ttl:PT5M}") Duration ttl) {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(CacheNames.ALL.stream()
                .map(name -> new TtlCache(name, ttl))
                .toList());
        return manager;
    }
}
