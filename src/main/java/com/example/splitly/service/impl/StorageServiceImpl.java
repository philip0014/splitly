package com.example.splitly.service.impl;

import com.example.splitly.properties.ApplicationProperties;
import com.example.splitly.service.StorageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageServiceImpl implements StorageService {

    private final String DELIMITER_SLASH = "/";
    private final String IMAGE_EXTENSION = ".png";
    private final String PROFILE_IMAGE_PATH = "image/profile";

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public String saveImage(String filename, MultipartFile multipartFile) throws IOException {
        String fullPath = String
            .join(DELIMITER_SLASH, applicationProperties.getBaseStoragePath(), PROFILE_IMAGE_PATH,
                filename);
        fullPath += IMAGE_EXTENSION;

        Path path = Paths.get(fullPath);
        byte[] fileBytes = multipartFile.getBytes();

        File file = new File(fullPath);
        if(file.exists() && !file.isDirectory()) {
            file.delete();
        }
        Files.write(path, fileBytes);
        return DELIMITER_SLASH + fullPath;
    }

    @Override
    public byte[] getImage(String filename) throws IOException {
        String fullPath = String
            .join(DELIMITER_SLASH, applicationProperties.getBaseStoragePath(), PROFILE_IMAGE_PATH,
                filename);
//        fullPath += IMAGE_EXTENSION;

        File file = new File(fullPath);
        InputStream in = new FileInputStream(file);
        return IOUtils.toByteArray(in);
    }

}
