package com.example.nexus.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveImage(MultipartFile file);
}
