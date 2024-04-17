package com.example.nexus.service;

import com.example.nexus.model.payload.request.ProductRequest;

public interface EditProductService {
    void editProduct(Long productId, ProductRequest productRequest);

    void editProductCampaignDiscount(Long productId, Integer discount);
}