package com.example.nexus.service;

import com.example.nexus.model.payload.request.ProductCampaignRequest;
import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.model.payload.request.ProductsRequest;
import com.example.nexus.model.payload.response.AdminProductResponse;
import com.example.nexus.model.payload.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void addProduct(ProductRequest productRequest);
    void removeProductCampaign(Long productId);
    void addProductCampaign(Long productId, ProductCampaignRequest productCampaignRequest);
    Page<ProductResponse> getProducts(ProductsRequest productsRequest, Pageable pageable);
    Page<AdminProductResponse> getProductsAdmin(ProductsRequest productsRequest, Pageable pageable);
    void editProduct(Long productId, ProductRequest productRequest);
    void editProductCampaignDiscount(Long productId, Integer discount);
    void removeProduct(Long productId);
}