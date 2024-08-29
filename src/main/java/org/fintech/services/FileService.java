package org.fintech.services;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);
    public static Optional<String> toXML(Object obj) {
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
    public static void saveFile(String path, String text) {
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

}
