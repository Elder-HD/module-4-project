package org.example.service;

import org.example.cache.JedisLFUCache;
import org.example.domain.entity.City;
import org.example.repository.CityRepository;

public class CityService {
    private final CityRepository cityRepository;
    private final JedisLFUCache jedis;

    public CityService() {
        this.cityRepository = new CityRepository();
        this.jedis = new JedisLFUCache(1);
    }

    public City getCityById(int id) {
        City city;
        String cityKey = "city:" + id;

        if (jedis.checkCity(cityKey)) { // Перевірка наявності міста в кеші
            city = jedis.getCityFromCache(cityKey); // Отримуємо закешоване місто
        } else {
            city = cityRepository.getById(id); // Витягуємо місто з БД
            if (city != null) {
                jedis.cacheCity(city, cityKey);
            }
        }
        return city;
    }

}
