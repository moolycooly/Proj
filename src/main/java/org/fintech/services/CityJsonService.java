package org.fintech.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.fintech.pojo.City;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import java.util.Optional;

public class CityJsonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityJsonService.class);
    public static Optional<City> cityJsonParser(File file) {
        if(file==null) {
            LOGGER.warn("Попытка десериализации из null file");
            return Optional.empty();
        }
        try {
            LOGGER.info("Десериализация файла {} в {}",file.getName(),City.class.getSimpleName());
            City city = new ObjectMapper().readValue(file, City.class);
            LOGGER.info("Десериализация файла успешна");
            return Optional.of(city);
        } catch (IOException e) {
            LOGGER.warn("Не удалось десериализовать");
            return Optional.empty();
        }
    }
}
