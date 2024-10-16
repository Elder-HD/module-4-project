package org.example.service;

import org.example.cache.JedisLFUCache;
import org.example.domain.entity.City;
import org.example.domain.exceptions.EntityNotFoundException;
import org.example.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CityService implements CacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityService.class);
    private final CityRepository cityRepository;
    private final JedisLFUCache jedisLFU;

    public CityService(CityRepository cityRepository, JedisLFUCache jedisLFU) {
        this.cityRepository = cityRepository;
        this.jedisLFU = jedisLFU;
    }

    @Override
    public void process() {
        jedisLFU.flushCache();
        List<Integer> cityIds = List.of(1, 2, 2, 2, 2, 3, 3, 3, 4, 1, 5, 5, 5, 6, 7, 2, 8, 9, 10, 10, 6);
        cityIds.forEach(this::getCityById);
    }

    public City getCityById(int id) {
        if (id <= 0) {
            LOGGER.error("id must be greater than 0. Provided: {}", id);
            throw new IllegalArgumentException("id must be greater than 0");
        }
        City city;
        String cityKey = "city:" + id;
        LOGGER.info("request city with key: \"{}\"", cityKey);
        if (jedisLFU.isCityInCache(cityKey)) {
            LOGGER.debug("City with key \"{}\" was found in cache. Try to take from cache...", cityKey);
            city = jedisLFU.getCityFromCache(cityKey);
        } else {
            LOGGER.debug("City with key \"{}\" wasn't found in cache. Try to take from DB...", cityKey);
            city = cityRepository.getById(id).orElseThrow(() -> {
                LOGGER.error("City with id {} not found in DB", id);
                return new EntityNotFoundException("City with id %s not found".formatted(id));
            });
            jedisLFU.cacheCity(city, cityKey);
        }
        return city;
    }

    public void deleteById(int id) {
        cityRepository.delete(getCityById(id));
        String cityKey = "city:" + id;
        jedisLFU.removeFromCache(cityKey);
    }
}
