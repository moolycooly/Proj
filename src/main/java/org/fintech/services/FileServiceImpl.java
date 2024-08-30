package org.fintech.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
        if(obj==null) {
            LOGGER.warn("Попытка сериализации null объекта");
            return Optional.empty();
        }
        try {
            LOGGER.info("Сериалиализация объекта {} в XML",obj.getClass().getSimpleName());
            XmlMapper xmlMapper = new XmlMapper();
            String xml = xmlMapper.writeValueAsString(obj);
            LOGGER.info("Сериалиализация выполнена успешно");
            return Optional.of(xml);
        }
        catch (IOException e) {
            LOGGER.warn("Не удалось сереализовать");
            return Optional.empty();
        }
    }
    @Override
    public void saveFile(String path, String text) {
        if(path == null && text == null) {
            LOGGER.warn("Попытка сохранения файла с null path и null text");
            return;
        }
        if(path == null || text == null) {
            LOGGER.warn("Попытка сохранения файла с null " +
                    (path==null ? "path" : "text"));
            return;
        }
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(text);
            LOGGER.info("Файл {} сохранен успешно",path);
        } catch (IOException e) {
            LOGGER.warn("Не удалось сохранить файл");
        }
    }
    @Override
    public <T> Optional<T> jsonParser(File file, Class<T> classTo) {
        if(file==null) {
            LOGGER.warn("Попытка десериализации из null file");
            return Optional.empty();
        }
        try {
            LOGGER.info("Десериализация файла {} в {}",file.getName(),classTo.getSimpleName());
            T model = new ObjectMapper().readValue(file, classTo);
            LOGGER.info("Десериализация файла успешна");
            return Optional.of(model);
        } catch (IOException e) {
            LOGGER.warn("Не удалось десериализовать");
            return Optional.empty();
        }
    }


}
