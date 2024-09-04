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


        String pathError = "src/main/resources/city-error.json";
        Optional<City> cityError = fileService.jsonParser(new File(pathError), City.class);
        Optional<String> xmlError = fileService.toXML(cityError.orElse(null));
        fileService.saveFile(path, xmlError.orElse(null));

    }
}