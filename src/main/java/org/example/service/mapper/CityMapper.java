package org.example.service.mapper;

import org.example.cache.CityCountry;
import org.example.cache.Language;
import org.example.domain.entity.City;
import org.example.domain.entity.Country;
import org.example.domain.entity.CountryLanguage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CityMapper {

    public CityCountry toRedis(City city) {
        CityCountry res = new CityCountry();
        res.setId(city.getId());
        res.setName(city.getName());
        res.setPopulation(city.getPopulation());
        res.setDistrict(city.getDistrict());

        Country country = city.getCountry();
        res.setAlternativeCountryCode(country.getAlternativeCode());
        res.setContinent(country.getContinent());
        res.setCountryCode(country.getCode());
        res.setCountryName(country.getName());
        res.setCountryPopulation(country.getPopulation());
        res.setCountryRegion(country.getRegion());
        res.setCountrySurfaceArea(country.getSurfaceArea());
        Set<CountryLanguage> countryLanguages = country.getLanguages();
        Set<Language> languages = countryLanguages.stream().map(cl -> {
            Language language = new Language();
            language.setLanguage(cl.getLanguage());
            language.setIsOfficial(cl.getIsOfficial());
            language.setPercentage(cl.getPercentage());
            return language;
        }).collect(Collectors.toSet());
        res.setLanguages(languages);

        return res;
    }

    public City toCity (CityCountry cityCountry) {
        City city = new City();
        city.setId(cityCountry.getId());
        city.setName(cityCountry.getName());
        city.setPopulation(cityCountry.getPopulation());
        city.setDistrict(cityCountry.getDistrict());

        city.setCountry(toCountry(cityCountry));

        return city;
    }

    private Country toCountry(CityCountry cityCountry) {
        Country country = new Country();
        country.setId(cityCountry.getId());
        country.setCode(cityCountry.getCountryCode());
        country.setAlternativeCode(cityCountry.getAlternativeCountryCode());
        country.setName(cityCountry.getCountryName());
        country.setContinent(cityCountry.getContinent());
        country.setRegion(cityCountry.getCountryRegion());
        country.setSurfaceArea(cityCountry.getCountrySurfaceArea());
        country.setPopulation(cityCountry.getCountryPopulation());
        Set<Language> languages = cityCountry.getLanguages();
        Set<CountryLanguage> countryLanguages = languages.stream().map(l -> {
            CountryLanguage countryLanguage = new CountryLanguage();
            countryLanguage.setLanguage(l.getLanguage());
            countryLanguage.setIsOfficial(l.getIsOfficial());
            countryLanguage.setPercentage(l.getPercentage());
            return countryLanguage;
        }).collect(Collectors.toSet());
        country.setLanguages(countryLanguages);
        return country;
    }


}
