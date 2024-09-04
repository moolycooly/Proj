package org.fintech.services;

import java.io.File;
import java.util.Optional;

public interface FileService {
    Optional<String> toXML(Object obj);
    void saveFile(String path, String text);
    <T> Optional<T> jsonParser(File file, Class<T> classTo);

}
