package com.example.nexus.service;

import jakarta.servlet.http.HttpServletRequest;

public interface SaleService {
    void buyProduct(Long productId, HttpServletRequest servletRequest);
}