package org.fintech;

import org.fintech.pojo.City;
import org.fintech.services.CityJsonService;
import org.fintech.services.FileService;

import java.io.File;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        String path = "src/main/resources/city.json";
        Optional<City> city = CityJsonService.cityJsonParser(new File(path));
        Optional<String> xml = FileService.toXML(city.orElse(null));
        FileService.saveFile("template.xml", xml.orElse(null));

    }
}