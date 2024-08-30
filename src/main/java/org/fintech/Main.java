package org.fintech;

import org.fintech.pojo.City;
import org.fintech.services.FileService;
import org.fintech.services.FileServiceImpl;

import java.io.File;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        FileService fileService = new FileServiceImpl();
        String path = "src/main/resources/city.json";
        Optional<City> city = fileService.jsonParser(new File(path), City.class);
        Optional<String> xml = fileService.toXML(city.orElse(null));
        fileService.saveFile("template.xml", xml.orElse(null));

    }
}