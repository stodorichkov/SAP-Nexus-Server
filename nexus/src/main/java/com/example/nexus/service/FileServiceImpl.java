package com.example.nexus.service;

import com.example.nexus.constant.ImageConstants;
import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.BadRequestException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    @SneakyThrows
    public String saveImage(MultipartFile file) {
        final var originalFilename = Optional
                .ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new BadRequestException(MessageConstants.INVALID_FILENAME));
        final var fileName = this.generateFileName(originalFilename);
        final var filePath = this.generatePath(fileName);

        final var convertedFile = new File(filePath);
        file.transferTo(convertedFile);

        return this.generateUrl(fileName);
    }

    private String generateFileName(String originalFilename) {
        return ImageConstants.PREFIX +
                UUID.randomUUID().toString().substring(0, 4) +
                Instant.now().toEpochMilli() +
                this.getExtension(originalFilename);
    }

    private String getExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }


    private String generatePath(String fileName) {
        return ImageConstants.PATH + fileName;
    }

    private String generateUrl(String filename) {
        return ImageConstants.URL + filename;
    }
}
