package org.fintech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class FileServiceImpl implements FileService{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public Optional<String> toXML(Object obj) {
        LOGGER.debug("Вход в метод toXML");
        try {
            if(obj==null) {
                LOGGER.info("Попытка сериализации null объекта");
                return Optional.empty();
            }
            LOGGER.info("Сериалиализация объекта {} в XML",obj.getClass().getSimpleName());
            XmlMapper xmlMapper = new XmlMapper();
            String xml = xmlMapper.writeValueAsString(obj);
            LOGGER.info("Сериалиализация выполнена успешно");
            return Optional.of(xml);
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
        finally {
            LOGGER.debug("Выход из метода toXML");
        }
    }
    @Override
    public void saveFile(String path, String text) {
        LOGGER.debug("Вход в метод saveFile");
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(text);
            LOGGER.info("Файл {} сохранен успешно",path);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        finally {
            LOGGER.debug("Выход из метода saveFile");
        }

    }
    @Override
    public <T> Optional<T> jsonParser(File file, Class<T> classTo) {
        LOGGER.debug("Вход в метод jsonParcer");
        try {
            if(file==null) {
                LOGGER.info("Попытка десериализации из null file");
                return Optional.empty();
            }
            LOGGER.info("Десериализация файла {} в {}",file.getName(),classTo.getSimpleName());
            T model = new ObjectMapper().readValue(file, classTo);
            LOGGER.info("Десериализация файла успешна");
            return Optional.of(model);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
        finally {
            LOGGER.debug("Выход из метода jsonParcer");
        }
    }


}
