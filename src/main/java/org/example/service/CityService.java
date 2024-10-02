package org.example.service;

import org.example.cache.JedisLFUCache;
import org.example.domain.entity.City;
import org.example.domain.exceptions.EntityNotFoundException;
import org.example.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityService.class);
    private final CityRepository cityRepository;
    private final JedisLFUCache jedis;

    public CityService(CityRepository cityRepository, JedisLFUCache jedis) {
        this.cityRepository = cityRepository;
        this.jedis = jedis;
    }

    public City getCityById(int id) {
        if (id <= 0) {
            LOGGER.error("id must be greater than 0. Provided: {}", id);
            throw new IllegalArgumentException("id must be greater than 0");
        }
        City city;
        String cityKey = "city:" + id;
        LOGGER.info("request city with key: \"{}\"", cityKey);
        if (jedis.isCityInCache(cityKey)) {
            LOGGER.debug("City with key \"{}\" was found in cache. Try to take from cache...", cityKey);
            city = jedis.getCityFromCache(cityKey);
        } else {
            LOGGER.debug("City with key \"{}\" wasn't found in cache. Try to take from DB...", cityKey);
            city = cityRepository.getById(id).orElseThrow(() -> {
                LOGGER.error("City with id {} not found in DB", id);
                return new EntityNotFoundException("City with id %s not found".formatted(id));
            });
            jedis.cacheCity(city, cityKey);
        }
        return city;
    }

    public void deleteById(int id) {
        cityRepository.delete(getCityById(id));
        String cityKey = "city:" + id;
        jedis.removeFromCache(cityKey);
    }

}
