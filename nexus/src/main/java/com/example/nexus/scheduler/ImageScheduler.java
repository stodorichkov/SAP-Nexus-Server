package com.example.nexus.scheduler;

import com.example.nexus.config.ImageConfig;
import com.example.nexus.model.entity.Product;
import com.example.nexus.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ImageScheduler implements InitializingBean {
    private final ProductRepository productRepository;
    private final ImageConfig imageConfig;

    @Scheduled(cron = "* * 1 * * ?")
    public void deleteImage() {
        final var dbImageFileNames = this.productRepository.findAll()
                .stream().map(Product::getImageLink).toList();

        final var fsImageFileNames = Arrays.stream(Objects.requireNonNull(
                new File(this.imageConfig.getDir()).list())).toList();

        for (var fileName : fsImageFileNames) {
            if (!dbImageFileNames.contains(imageConfig.getBaseUrl() + fileName)) {
                final var file = new File(imageConfig.getDir() + '/' + fileName);
                file.delete();
            }
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        deleteImage();
    }
}
