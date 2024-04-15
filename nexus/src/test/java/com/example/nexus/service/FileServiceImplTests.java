package com.example.nexus.service;

import com.example.nexus.config.ImageConfig;
import com.example.nexus.exception.FileUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplTests {
    private static String path;
    private static String baseUrl;

    @Mock
    private ImageConfig imageConfig;
    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        path = "src/test/resources/image/test.png";
        baseUrl = "http://localhost/images/";
    }

    @Test
    void upload_invalidFile_expectFileUploadException() {
        final var content = new byte[]{};
        final var file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                content
        );

        assertThrows(FileUploadException.class, () -> this.fileService.upload(file));
    }

    @Test
    void upload_invalidDir_expectNullPointerException() {
        try {
            final var content = Files.readAllBytes(Paths.get(path));
            final var file = new MockMultipartFile(
                    "file",
                    "test.png",
                    "image/png",
                    content
            );

            when(this.imageConfig.getDir()).thenReturn(null);

            assertThrows(NullPointerException.class, () -> this.fileService.upload(file));
        } catch (IOException ignored) {
        }
    }

    @Test
    void upload_validFile_expectUpload() {
        try {
            final var content = Files.readAllBytes(Paths.get(path));
            final var file = new MockMultipartFile(
                    "file",
                    "test.png",
                    "image/png",
                    content
            );

            when(this.imageConfig.getDir()).thenReturn("/path/to/images");
            lenient().when(this.imageConfig.getBaseUrl()).thenReturn(baseUrl);

            final var result = this.fileService.upload(file);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.startsWith(baseUrl + "NEX")),
                    () -> assertTrue(result.endsWith(".png"))
            );
        } catch (IOException ignored) {
        }
    }
}