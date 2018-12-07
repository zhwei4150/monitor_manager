package com.bonree.ants.manager.server.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DataCacheManager {

    private Map<String, Cache<Long, String>> caches;

    public DataCacheManager(List<String> taskNames) {
        caches = new ConcurrentHashMap<>();
        taskNames.stream().forEach(taskName -> {
            Cache<Long, String> cache = CacheBuilder.newBuilder().initialCapacity(1024).concurrencyLevel(1)
                    .expireAfterWrite(3600, TimeUnit.SECONDS).build();
            caches.put(taskName, cache);
        });

    }

    public boolean putCache(String taskName, Long timestamp, String jsonContent) {
        if (caches.get(taskName) != null) {
            caches.get(taskName).put(timestamp, jsonContent);
            return true;
        }
        return false;
    }

    public String getCache(String taskName, Long timestamp) {
        return caches.get(taskName) != null ? caches.get(taskName).getIfPresent(timestamp) : null;
    }

}
