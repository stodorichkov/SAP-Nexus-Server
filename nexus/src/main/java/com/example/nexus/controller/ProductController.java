package com.example.nexus.controller;

import com.example.nexus.model.payload.request.ProductsRequest;
import com.example.nexus.model.payload.response.ProductResponse;
import com.example.nexus.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public Page<ProductResponse> getProducts(ProductsRequest productsRequest, Pageable pageable) {
        return this.productService.getProducts(productsRequest, pageable);
    }
}