package com.example.splitly.web;

import com.example.splitly.model.ApiPath;
import com.example.splitly.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController(value = "StorageRestController")
@RequestMapping(ApiPath.BASE_STORAGE)
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping(value = "/image/profile/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getImageProfile(@PathVariable String filename) throws IOException {
        return storageService.getImage(filename);
    }

}
