package com.example.nexus.controller;

import com.example.nexus.service.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sale")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping("/product/{productId}")
    ResponseEntity<?> buyProduct(@PathVariable Long productId, HttpServletRequest servletRequest) {
        this.saleService.buyProduct(productId, servletRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}