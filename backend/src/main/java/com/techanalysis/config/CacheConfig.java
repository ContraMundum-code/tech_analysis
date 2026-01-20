package com.techanalysis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 缓存配置 - 使用内存缓存
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    /**
     * 使用内存缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        log.info("使用内存缓存 (ConcurrentMapCacheManager)");
        return new ConcurrentMapCacheManager("techmap", "trend", "category", "stats", "network", "hottech", "maturity", "region");
    }
}
