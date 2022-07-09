package com.mca.yourapp.conf;

import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.dto.LogType;
import com.mca.yourapp.service.impl.LogServiceImpl;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class CustomCacheErrorHandler implements CacheErrorHandler {

    /**
     * Why is this logService not Autowired?
     * Simply because at this step LogService bean is not yet instantiated (it would be null)
     * logService CAN NOT be null, be extremely careful if you want to edit this,
     * and check regressions launching a simple test with the cache not available.
     * */
    private final LogService logService = new LogServiceImpl();

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logWarning("Error with cache get method: " + exception.getMessage());
    }


    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        logWarning("Error with cache put method: " + exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        logWarning("Error with cache evict method: " + exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        logWarning("Error with cache clear method: " + exception.getMessage());
    }

    private void logWarning(final String message) {
        logService.log(LogType.WARNING, message);
    }
}
