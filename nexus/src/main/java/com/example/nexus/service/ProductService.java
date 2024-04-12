package com.example.nexus.service;

import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void addProduct(ProductRequest productRequest);
    Page<ProductResponse> getProducts(Pageable pageable);
    Page<ProductResponse> getPromoProducts(Pageable pageable);
    Page<ProductResponse> getProductsByCampaign(Pageable pageable, String campaignName);
}