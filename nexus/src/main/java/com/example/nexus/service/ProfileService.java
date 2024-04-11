package com.example.nexus.service;


import com.example.nexus.model.payload.request.AddMoneyRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface ProfileService {
    void addMoney(AddMoneyRequest addMoneyRequest, HttpServletRequest request);
}