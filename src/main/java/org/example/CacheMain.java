package org.example;

import org.example.cache.JedisLFUCache;
import org.example.repository.CityRepository;
import org.example.service.CityService;
import org.example.service.FullCacheService;

public class CacheMain {


    public static void main(String[] args) {
        FullCacheService fullCache = new FullCacheService();
        fullCache.fullCacheTest();
//        lfuCacheTest(); // run 2 times to see getting cached values
    }

    private static void lfuCacheTest() {
        CityService cityService = new CityService(new CityRepository(), new JedisLFUCache(1));
        cityService.getCityById(1);
        cityService.getCityById(2);
        cityService.getCityById(3);
        cityService.getCityById(3);
        cityService.getCityById(4);
        cityService.getCityById(1);
        cityService.getCityById(5);
        cityService.getCityById(6);
        cityService.getCityById(7);
    }

}