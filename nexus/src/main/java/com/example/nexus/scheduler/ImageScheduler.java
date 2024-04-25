package com.example.nexus.scheduler;

import com.example.nexus.config.ImageConfig;
import com.example.nexus.model.entity.Product;
import com.example.nexus.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ImageScheduler implements InitializingBean {
    private final ProductRepository productRepository;
    private final ImageConfig imageConfig;
    private final Logger logger = LoggerFactory.getLogger(ImageScheduler.class);

    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteImage() {
        List<String> dbImageFileNames = this.productRepository.findAll()
                .stream().map(Product::getImageLink).toList();

        List<String> fsImageFileNames = Arrays.stream(Objects.requireNonNull(
                new File(this.imageConfig.getBaseUrl()).list()))
                .map(n -> this.imageConfig.getBaseUrl() + n).toList();

        for (String fileName : fsImageFileNames) {
            if (!dbImageFileNames.contains(fileName)) {
                File file = new File(fileName);
                if (file.delete()) {
                    this.logger.info("File deleted!");
                } else {
                    this.logger.warn("File deletion failed!");
                }
            }
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        deleteImage();
    }
}
