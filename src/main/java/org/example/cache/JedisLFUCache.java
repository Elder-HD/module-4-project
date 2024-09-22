package org.example.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.entity.City;
import org.example.service.mapper.CityMapper;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class JedisLFUCache {
    private final Jedis jedis;
    private final CityMapper cityMapper;
    private final ObjectMapper jsonMapper;
    public final int CACHE_CAPACITY = 5;

    public JedisLFUCache(int db) {
        this.cityMapper = new CityMapper();
        this.jsonMapper = new ObjectMapper();
        this.jedis = new Jedis("localhost", 6379);
        jedis.select(db);
    }

    public boolean checkCity(String cityKey) {
        return jedis.exists(cityKey);
    }

    public City getCityFromCache(String cityKey) {
        jedis.hincrBy(cityKey, "count", 1); // Інкремент лічильника звернень
        City city = null;
        try {
            String cityJson = jedis.hget(cityKey, "value");
            CityCountry cityCountry = jsonMapper.readValue(cityJson, CityCountry.class);// Десеріалізація
            city = cityMapper.toCity(cityCountry);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return city;
    }

    public void cacheCity(City city, String cityKey) {
        // Перевіряємо, чи не переповнений кеш
        if (jedis.keys("city:*").size() >= CACHE_CAPACITY) {
            removeLeastFrequentlyUsedCity();
        }
        // Додаємо місто в кеш і ініціалізуємо лічильник
        try {
            CityCountry cityCountry = cityMapper.toCityCountry(city);
            String cityJson = jsonMapper.writeValueAsString(cityCountry);
            jedis.hset(cityKey, "value", cityJson);
            jedis.hset(cityKey, "count", "1"); // Ініціалізуємо лічильник запитів
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void removeLeastFrequentlyUsedCity() {
        Set<String> countKeys = jedis.keys("city:*");

        String cityId = null;
        int minCount = Integer.MAX_VALUE;

        // Шукаємо ключ із найменшим значенням лічильника
        for (String key : countKeys) {
            int count = Integer.parseInt(jedis.hget(key, "count"));
            if (count < minCount) {
                minCount = count;
                cityId = key;
            }
        }
        if (cityId != null) {
            jedis.del(cityId); // Видаляємо місто з кешу
        }
    }
}
