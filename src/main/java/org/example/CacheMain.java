package org.example;

import org.example.cache.CityCountry;
import org.example.domain.entity.City;
import org.example.service.CityService;
import org.example.service.FullCacheService;

import java.util.List;

public class CacheMain {


    public static void main(String[] args) {
//        fullCacheTest();
        lfuCacheTest();
    }

    private static void lfuCacheTest() {
        CityService cityService = new CityService();
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

    private static void fullCacheTest() {
        FullCacheService cacheService = new FullCacheService();
        List<City> allCities = cacheService.fetchData();
        List<CityCountry> preparedData = cacheService.transformData(allCities);
        cacheService.pushToRedis(preparedData);

        cacheService.getSessionFactory().getCurrentSession().close();

        List<Integer> ids = List.of(3, 100, 441, 6, 2200, 1323, 10, 102, 2532, 2533);

        long startRedis = System.currentTimeMillis();
        cacheService.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        cacheService.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        cacheService.shutdown();
    }


}