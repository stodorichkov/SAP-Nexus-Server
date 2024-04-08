package com.example.nexus.service;


import com.example.nexus.model.entity.Wallet;

public interface WalletService {
    Wallet createWallet(Long userId);

    Wallet getWalletByUserId(Long userId);

    Wallet addMoney(Long userId, double amount);
}