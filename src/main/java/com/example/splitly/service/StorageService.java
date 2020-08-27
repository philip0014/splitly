package com.example.splitly.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String saveImage(String filename, MultipartFile file) throws IOException;

    byte[] getImage(String filename) throws IOException;

}
