package com.example.nexus.scheduler;

import com.example.nexus.config.ImageConfig;
import com.example.nexus.model.entity.Product;
import com.example.nexus.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ImageScheduler implements InitializingBean {
    private final ProductRepository productRepository;
    private final ImageConfig imageConfig;
    private final Logger logger = LoggerFactory.getLogger(ImageScheduler.class);

    @Scheduled(cron = "5 * * * * ?")
    public void deleteImage() {
        final var dbImageFileNames = this.productRepository.findAll()
                .stream().map(Product::getImageLink).toList();

        final var fsImageFileNames = Arrays.stream(Objects.requireNonNull(
                new File(this.imageConfig.getDir()).list())).toList();

        this.logger.info(String.valueOf(dbImageFileNames));
        this.logger.info(String.valueOf(fsImageFileNames));

        for (var fileName : fsImageFileNames) {
            if (!dbImageFileNames.contains(imageConfig.getBaseUrl() + fileName)) {
                File file = new File(imageConfig.getDir() + '/' + fileName);
                logger.info(file.toString());
                file.delete();
            }
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        deleteImage();
    }
}
