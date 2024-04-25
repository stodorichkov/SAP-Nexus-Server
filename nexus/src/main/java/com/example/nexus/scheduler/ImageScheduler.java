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
    ProductRepository productRepository;
    ImageConfig imageConfig;
    Logger logger = LoggerFactory.getLogger(ImageScheduler.class);

    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteImage() {
        List<String> dbImageFileNames = productRepository.findAll()
                .stream().map(Product::getImageLink).toList();

        List<String> fsImageFileNames = Arrays.stream(Objects.requireNonNull(
                new File(imageConfig.getBaseUrl()).list()))
                .map(n -> imageConfig.getBaseUrl() + n).toList();

        for (String fileName : fsImageFileNames) {
            if (!dbImageFileNames.contains(fileName)) {
                File file = new File(fileName);
                if (file.delete()) {
                    logger.info("File deleted!");
                } else {
                    logger.warn("File deletion failed!");
                }
            }
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        deleteImage();
    }
}
