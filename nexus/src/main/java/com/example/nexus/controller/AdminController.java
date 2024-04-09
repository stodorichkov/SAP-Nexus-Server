package com.example.nexus.controller;

import com.example.nexus.model.payload.request.ProductRequest;
import com.example.nexus.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;

    @PostMapping(value = "/product")
    ResponseEntity<?> addProduct(@Valid @ModelAttribute ProductRequest productRequest) {
        productService.addProduct(productRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
