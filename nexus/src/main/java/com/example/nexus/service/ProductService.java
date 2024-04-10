package com.example.nexus.service;

import com.example.nexus.model.payload.request.ProductRequest;

public interface ProductService {
    void addProduct(ProductRequest productRequest);
}