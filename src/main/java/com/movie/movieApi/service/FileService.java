package com.movie.movieApi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {

    String uploadFile(MultipartFile imageFile) throws IOException;

    InputStream getResourceFile(String imageName) throws FileNotFoundException;

    public void deleteImage(String imageName) throws IOException;
}
