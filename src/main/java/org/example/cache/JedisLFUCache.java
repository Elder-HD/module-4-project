package org.example.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.entity.City;
import org.example.domain.exceptions.EntityConversionException;
import org.example.service.mapper.CityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class JedisLFUCache {
    private static final int PORT = 6379;
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisLFUCache.class);
    private final Jedis jedis;
    private final CityMapper cityMapper;
    private final ObjectMapper jsonMapper;
    private final int cacheCapacity;

    public JedisLFUCache(int db, int cacheCapacity) {
        if (db < 0 || db > 16) {
            LOGGER.error("DB index is out of range. Only 0-15 DBs are available. Current DB: {}", db);
            throw new IllegalArgumentException("DB index is out of range. Only 0-15 DBs are available");
        }
        this.cityMapper = new CityMapper();
        this.jsonMapper = new ObjectMapper();
        this.jedis = new Jedis("localhost", PORT);
        this.cacheCapacity = cacheCapacity;
        jedis.select(db);
        LOGGER.info("Connection with Jedis client established. {} DB is using. Cache capacity = {}", db, cacheCapacity);
    }

    public boolean isCityInCache(String cityKey) {
        return jedis.exists(cityKey);
    }

    public City getCityFromCache(String cityKey) {
        jedis.hincrBy(cityKey, "count", 1);
        LOGGER.info("City with key \"{}\" was requested {} times", cityKey, jedis.hget(cityKey, "count"));
        City city;
        try {
            String cityJson = jedis.hget(cityKey, "value");
            CityCountry cityCountry = jsonMapper.readValue(cityJson, CityCountry.class);
            city = cityMapper.toCity(cityCountry);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error during Json deserialization for city {}", cityKey, e.getCause());
            throw new EntityConversionException("Error during Json deserialization");
        }
        return city;
    }

    public void cacheCity(City city, String cityKey) {
        if (jedis.keys("city:*").size() >= cacheCapacity) {
            removeLeastFrequentlyUsedCity();
        }
        try {
            CityCountry cityCountry = cityMapper.toCityCountry(city);
            String cityJson = jsonMapper.writeValueAsString(cityCountry);
            jedis.hset(cityKey, "value", cityJson);
            jedis.hset(cityKey, "count", "1");
            LOGGER.debug("City {} added to cache", cityKey);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error during Json serialization for city {}", cityKey, e.getCause());
            throw new EntityConversionException("Error during Json serialization");
        }
    }

    public void removeLeastFrequentlyUsedCity() {
        Set<String> countKeys = jedis.keys("city:*");
        LOGGER.debug("{} cities already cached. Capacity = {}", countKeys.size(), cacheCapacity);

        String cityKey = null;
        int minCount = Integer.MAX_VALUE;

        for (String key : countKeys) {
            int count = Integer.parseInt(jedis.hget(key, "count"));
            if (count < minCount) {
                minCount = count;
                cityKey = key;
            }
        }
        if (cityKey != null) {
            jedis.del(cityKey);
            LOGGER.debug("City {} removed from cache", cityKey);
        }
    }

    public void removeFromCache(String cityKey) {
        jedis.del(cityKey);
    }

    public void flushCache() {
        jedis.flushDB();
    }
}
