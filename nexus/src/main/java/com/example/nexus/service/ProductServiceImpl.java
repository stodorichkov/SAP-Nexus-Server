package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.ProductMapper;
import com.example.nexus.model.payload.request.ProductCampaignRequest;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.AdminProductResponse;
import com.example.nexus.model.payload.response.ProductResponse;
import com.example.nexus.repository.CampaignRepository;
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
    private final CampaignRepository campaignRepository;

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
    public void addProductCampaign(Long productId, ProductCampaignRequest productCampaignRequest) {
        final var product = this.productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PRODUCT_NOT_FOUND));

        final var campaign = this.campaignRepository
                .findByName(productCampaignRequest.campaignName())
                .orElseThrow(() -> new NotFoundException(MessageConstants.CAMPAIGN_NOT_FOUND));

        product.setCampaign(campaign);
        product.setCampaignDiscount(productCampaignRequest.campaignDiscount());
        if (campaign.getIsActive()) {
            product.setDiscount(productCampaignRequest.campaignDiscount());
        }

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

        if (campaign != null) {
            if (campaign.getIsActive()) {
                product.setDiscount(discount);
            }
            product.setCampaignDiscount(discount);
        }

        this.productRepository.save(product);
    }

    @Override
    public void removeProduct(Long productId) {
        final var product = this.productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PRODUCT_NOT_FOUND));

        this.productRepository.delete(product);
    }
}