package org.example.service;

import org.example.cache.JedisLFUCache;
import org.example.domain.entity.City;
import org.example.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityService.class);
    private final CityRepository cityRepository;
    private final JedisLFUCache jedis;

    public CityService() {
        this.cityRepository = new CityRepository();
        this.jedis = new JedisLFUCache(1);
    }

    public City getCityById(int id) {
        if (id <= 0) {
            LOGGER.error("id must be greater than 0. Provided: {}", id);
            throw new IllegalArgumentException("id must be greater than 0");
        }
        City city;
        String cityKey = "city:" + id;
        LOGGER.info("request city with key: {}", cityKey);
        if (jedis.isCityInCache(cityKey)) {
            LOGGER.debug("City with key \"{}\" was found in cache. Try to take from cache...", cityKey);
            city = jedis.getCityFromCache(cityKey);
        } else {
            LOGGER.debug("City with key \"{}\" wasn't found in cache. Try to take from DB...", cityKey);
            city = cityRepository.getById(id);
            if (city != null) {
                jedis.cacheCity(city, cityKey);
            }
        }
        return city;
    }

    //TODO: On the future: Add other CRUD methods.

}
