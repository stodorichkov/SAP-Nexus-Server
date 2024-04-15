package com.example.nexus.service;

import com.example.nexus.model.payload.request.TurnoverRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface SaleService {
    void buyProduct(Long productId, HttpServletRequest servletRequest);
    Float getTurnover(TurnoverRequest request);
}