package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cache.CityCountry;
import org.example.domain.entity.City;
import org.example.repository.CityRepository;
import org.example.service.mapper.CityMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;

public class CityService {
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final ObjectMapper jsonMapper;
    private final Jedis jedis;
//    private PriorityQueue<Integer> cityQueue;

    public final int CACHE_CAPACITY = 5;

    public CityService(CityRepository cityRepository) {
        this.jedis = new Jedis("localhost", 6379);
//        jedis.select(1);
        this.cityRepository = new CityRepository();
        this.jsonMapper = new ObjectMapper();
        this.cityMapper = new CityMapper();
//        this.cityQueue = new PriorityQueue<>(Comparator.comparingInt(Integer::valueOf));
    }

    public City getCityById(int id) {
        City city;
        String cityKey = "city:" + id;
        String countKey = "city_count:" + id;

        if (jedis.exists(cityKey)) { // Перевірка наявності міста в кеші
            jedis.incr(countKey); // Інкремент лічильника звернень
            city = getCityFromCache(cityKey); // Отримуємо закешоване місто
        } else {
            city = cityRepository.getById(id); // Витягуємо місто з БД
            if (city != null) {
                cacheCity(city, cityKey, countKey);
            }
        }
        return city;
    }

    private void cacheCity(City city, String cityKey, String countKey) {
        // Перевіряємо, чи не переповнений кеш
        if (jedis.keys("city:*").size() >= CACHE_CAPACITY) {
            removeLeastFrequentlyUsedCity();
        }
        // Додаємо місто в кеш і ініціалізуємо лічильник
        try {
            CityCountry cityCountry = cityMapper.toRedis(city);
            String cityJson = jsonMapper.writeValueAsString(cityCountry);
            jedis.set(cityKey, cityJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        jedis.set(countKey, "1");  // Ініціалізуємо лічильник запитів
    }

    private City getCityFromCache(String cityKey) {
        City city = null;
        String cityJson = jedis.get(cityKey);
        try {
            CityCountry cityCountry = jsonMapper.readValue(cityJson, CityCountry.class);// Десеріалізація
            city = cityMapper.toCity(cityCountry);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return city;
    }

    private void removeLeastFrequentlyUsedCity() {
        Set<String> countKeys = jedis.keys("city_count:*");
        String minCountKey = null;
        int minCount = Integer.MAX_VALUE;

        // Шукаємо ключ із найменшим значенням лічильника
        for (String key : countKeys) {
            int count = Integer.parseInt(jedis.get(key));
            if (count < minCount) {
                minCount = count;
                minCountKey = key;
            }
        }
        if (minCountKey != null) {
            // Видаляємо місто з кешу
            String cityId = minCountKey.replace("city_count:", "");
            jedis.del("city:" + cityId);
            jedis.del(minCountKey);
        }
    }


}
