package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.repository.CategoryRepository;
import com.example.nexus.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditProductServiceImpl implements EditProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Override
    public void editProduct(Long productId, ProductRequest productRequest) {
        final var product = this.productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PRODUCT_NOT_FOUND));

        final var category = this.categoryRepository
                .findByName(productRequest.category())
                .orElseThrow(() -> new NotFoundException(MessageConstants.CATEGORY_NOT_FOUND));

        final var imageUrl = this.fileService.upload(productRequest.image());

        product.setName(productRequest.name());
        product.setBrand(productRequest.brand());
        product.setCategory(category);
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setMinPrice(productRequest.minPrice());
        product.setDiscount(productRequest.discount());
        product.setAvailability(productRequest.availability());
        product.setImageLink(imageUrl);

        this.productRepository.save(product);
    }

    @Override
    public void editProductCampaignDiscount(Long productId, Integer discount) {
        final var product = this.productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PRODUCT_NOT_FOUND));

        final var campaign = product.getCampaign();

        if (campaign != null && campaign.getIsActive()) {
            product.setDiscount(discount);
        }

        this.productRepository.save(product);
    }
}