package com.example.nexus.service;

import com.example.nexus.config.ImageConfig;
import com.example.nexus.constant.ImageConstants;
import com.example.nexus.constant.SingleSymbolConstants;
import com.example.nexus.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final ImageConfig imageConfig;

    @Override
    public String upload(MultipartFile file) {
        final var img = this.validate(file);
        final var filename = this.generateFileName();
        final var extension = this.getExtension(file);
        final var path = this.generatePath(filename, extension);

        this.saveImage(img, extension, path);

        return generateUrl(path.getFileName().toString());
    }

    @SneakyThrows
    private BufferedImage validate(MultipartFile file) {
        return Optional.ofNullable(ImageIO.read(file.getInputStream()))
                .orElseThrow(FileUploadException::new);
    }

    private String generateFileName() {
        return ImageConstants.PREFIX +
                UUID.randomUUID().toString().substring(0, 4) +
                Instant.now().toEpochMilli();
    }

    private String getExtension(MultipartFile file) {
        return Objects
                .requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(SingleSymbolConstants.DOT));
    }

    @SneakyThrows
    private Path generatePath(String fileName, String extension) {
        final var path = Paths.get(this.imageConfig.getDir(), fileName + extension);
        final var dir = path.getParent();

        if(dir != null) {
            Files.createDirectories(dir);
        }

        return path;
    }

    @SneakyThrows
    private void saveImage(BufferedImage img, String extension, Path filePath) {
        ImageIO.write(
                img,
                extension.replace(
                        SingleSymbolConstants.DOT,
                        SingleSymbolConstants.EMPTY_STRING
                ),
                filePath.toFile()
        );
    }

    private String generateUrl(String filename) {
        return imageConfig.getBaseUrl() + filename;
    }
}