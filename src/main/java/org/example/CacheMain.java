package org.example;

import org.example.cache.JedisLFUCache;
import org.example.repository.CityRepository;
import org.example.service.CacheService;
import org.example.service.CityService;
import org.example.service.FullCacheService;

public class CacheMain {
    public static final int JEDIS_DB = 2;
    public static final int JEDIS_CACHE_CAPACITY = 5;

    public static void main(String[] args) {
        CacheService fullCache = new FullCacheService();
        fullCache.process();

        CityRepository repository = new CityRepository();
        JedisLFUCache jedis = new JedisLFUCache(JEDIS_DB, JEDIS_CACHE_CAPACITY);
        CacheService lfuCache = new CityService(repository, jedis);
        lfuCache.process();
    }
}