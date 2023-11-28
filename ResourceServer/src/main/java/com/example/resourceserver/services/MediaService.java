package com.example.resourceserver.services;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String saveImage(MultipartFile file);
}
