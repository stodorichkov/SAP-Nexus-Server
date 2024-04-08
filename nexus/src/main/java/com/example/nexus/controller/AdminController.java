package com.example.nexus.controller;

import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;

    @PostMapping(value = "/product")
    ResponseEntity<?> addProduct(@Valid @RequestBody ProductRequest productRequest) {
        productService.addProduct(productRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
