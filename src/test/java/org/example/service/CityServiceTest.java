package org.example.service;

import org.example.cache.JedisLFUCache;
import org.example.domain.entity.City;
import org.example.domain.exceptions.EntityNotFoundException;
import org.example.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {
    @Mock
    private CityRepository cityRepository;
    @Mock
    private JedisLFUCache jedis;
    @InjectMocks
    private CityService cityService;

    @Test
    public void getByIdWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> cityService.getCityById(-10));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cityService.getCityById(0));

        assertEquals("id must be greater than 0",exception.getMessage());

        verify(cityRepository, never()).getById(any());
        verify(jedis, never()).getCityFromCache(any());
    }

    @Test
    public void getCityByIdFromCache() {
        int id = 1;
        String cityKey = "city:" + id;
        City cityExpected = new City();
        when(jedis.isCityInCache(cityKey)).thenReturn(true);
        when(jedis.getCityFromCache(cityKey)).thenReturn(cityExpected);

        City cityActual = cityService.getCityById(id);

        assertNotNull(cityActual);
        assertEquals(cityExpected, cityActual);
        verify(jedis, times(1)).getCityFromCache(cityKey);
        verify(cityRepository, never()).getById(id);
    }

    @Test
    public void getCityByIdFromDb() {
        int id = 1;
        String cityKey = "city:" + id;
        City cityExpected = new City();
        when(jedis.isCityInCache(cityKey)).thenReturn(false);
        when(cityRepository.getById(id)).thenReturn(Optional.of(cityExpected));

        City cityActual = cityService.getCityById(id);

        assertNotNull(cityActual);
        assertEquals(cityExpected, cityActual);
        verify(cityRepository, times(1)).getById(id);
        verify(jedis, never()).getCityFromCache(cityKey);
    }

    @Test
    public void getCityByIdNotFound() {
        int id = 22222;
        String cityKey = "city:" + id;
        when(jedis.isCityInCache(cityKey)).thenReturn(false);
        when(cityRepository.getById(id)).thenReturn(Optional.ofNullable(null));

        assertThrows(EntityNotFoundException.class, () ->cityService.getCityById(id));
        verify(cityRepository, times(1)).getById(id);
    }


}