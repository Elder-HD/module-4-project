package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import lombok.Getter;
import org.example.cache.CityCountry;
import org.example.config.HibernateUtil;
import org.example.config.RedisUtil;
import org.example.domain.entity.City;
import org.example.domain.entity.CountryLanguage;
import org.example.domain.exceptions.EntityConversionException;
import org.example.domain.exceptions.EntityNotFoundException;
import org.example.repository.CityRepository;
import org.example.service.mapper.CityMapper;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * <u>Full DB Cache</u> realisation from guide. <br>
 * Run {@code fullCacheTest()} method in Main to compare processing speed of MySQL DB and Redis requests.
 */
public class FullCacheService implements CacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullCacheService.class);
    @Getter
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final RedisClient redisClient = RedisUtil.getLettuceClient();

    private final ObjectMapper jsonMapper;
    private final CityMapper cityMapper;
    private final CityRepository cityRepository;

    public FullCacheService() {
        cityRepository = new CityRepository();
        jsonMapper = new ObjectMapper();
        cityMapper = new CityMapper();
    }

    @Override
    public void process() {
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

    public List<City> fetchData() {
        List<City> cities = cityRepository.getAll();
        return cities;
    }

    public List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(cityMapper::toCityCountry).collect(Collectors.toList());
    }

    public void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), jsonMapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error during Json serialization for CityCountry in cache", e.getCause());
                    throw new EntityConversionException("Error during Json serialization");
                }
            }

        }
    }

    public void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    jsonMapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error during Json deserialization city from cache", e.getCause());
                    throw new EntityConversionException("Error during Json serialization");
                }
            }
        }
    }

    public void testMysqlData(List<Integer> ids) {
        for (Integer id : ids) {
            City city = cityRepository.getById(id).orElseThrow(() -> {
                LOGGER.error("City with id {} not found in DB",id);
                return new EntityNotFoundException("City with id %s not found".formatted(id));
            });
            Set<CountryLanguage> languages = city.getCountry().getLanguages();
        }
    }

    public void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}
