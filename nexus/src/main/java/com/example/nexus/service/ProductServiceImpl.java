package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.AdminProductResponse;
import com.example.nexus.model.payload.response.ProductResponse;
import com.example.nexus.repository.CategoryRepository;
import com.example.nexus.repository.ProductRepository;
import com.example.nexus.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;

    @Override
    public void addProduct(ProductRequest productRequest) {
        final var category = this.categoryRepository
                .findByName(productRequest.category())
                .orElseThrow(() -> new NotFoundException(MessageConstants.CATEGORY_NOT_FOUND));
        final var imageUrl = this.fileService.upload(productRequest.image());

        final var product = this.productMapper.productRequestToProduct(productRequest);
        product.setCategory(category);
        product.setImageLink(imageUrl);
        product.setDiscount(0);
        product.setCampaignDiscount(0);

        this.productRepository.save(product);
    }

    @Override
    public void removeProductCampaign(Long productId) {
        final var product = this.productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PRODUCT_NOT_FOUND));

        product.setCampaign(null);
        product.setDiscount(0);
        product.setCampaignDiscount(0);

        this.productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> getProducts(Pageable pageable) {
        final var specification = ProductSpecifications.findAvailable();

        return this.productRepository
                .findAll(specification, pageable)
                .map(this.productMapper::productToProductResponse);
    }

    @Override
    public Page<ProductResponse> getPromoProducts(Pageable pageable) {
        final var specification = ProductSpecifications.findPromos()
                .and(ProductSpecifications.findAvailable());

        return this.productRepository
                .findAll(specification, pageable)
                .map(this.productMapper::productToProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByCampaign(String campaignName, Pageable pageable) {
        final var specification = ProductSpecifications.findByCampaignName(campaignName)
                .and(ProductSpecifications.findAvailable());

        return this.productRepository
                .findAll(specification, pageable)
                .map(this.productMapper::productToProductResponse);
    }

    @Override
    public Page<AdminProductResponse> getProductsAdmin(Pageable pageable) {
        return this.productRepository
                .findAll(pageable)
                .map(this.productMapper::productToAdminProductResponse);
    }

    @Override
    public Page<AdminProductResponse> getProductsByCampaignAdmin(String campaignName, Pageable pageable) {
        final var specification = ProductSpecifications.findByCampaignName(campaignName);

        return this.productRepository
                .findAll(specification, pageable)
                .map(this.productMapper::productToAdminProductResponse);
    }
}