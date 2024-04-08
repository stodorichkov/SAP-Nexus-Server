package com.example.nexus.controller;

import com.example.nexus.model.entity.Wallet;
import com.example.nexus.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@RequestParam Long userId) {
        Wallet wallet = walletService.createWallet(userId);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long userId) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/{userId}/add-money")
    public ResponseEntity<Wallet> addMoney(@PathVariable Long userId, @RequestParam double amount) {
        Wallet updatedWallet = walletService.addMoney(userId, amount);
        return ResponseEntity.ok(updatedWallet);
    }

}
