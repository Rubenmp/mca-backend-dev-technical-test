package com.mca.yourapp.conf;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class CustomCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        // Do nothing or log error
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        // Do nothing or log error
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        // Do nothing or log error
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        // Do nothing or log error
    }
}
