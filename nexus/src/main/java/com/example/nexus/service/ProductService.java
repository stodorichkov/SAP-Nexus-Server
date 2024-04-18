package com.example.nexus.service;

import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.AdminProductResponse;
import com.example.nexus.model.payload.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void addProduct(ProductRequest productRequest);
    void removeProductCampaign(Long productId);
    Page<ProductResponse> getProducts(Pageable pageable);
    Page<ProductResponse> getPromoProducts(Pageable pageable);
    Page<ProductResponse> getProductsByCampaign(String campaignName, Pageable pageable);
    Page<AdminProductResponse> getProductsAdmin(Pageable pageable);
    Page<AdminProductResponse> getProductsByCampaignAdmin(String campaignName, Pageable pageable);
    void editProduct(Long productId, ProductRequest productRequest);
    void editProductCampaignDiscount(Long productId, Integer discount);
}