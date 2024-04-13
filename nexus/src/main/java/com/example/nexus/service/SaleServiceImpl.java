package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.exception.PaymentException;
import com.example.nexus.model.entity.Sale;
import com.example.nexus.repository.ProductRepository;
import com.example.nexus.repository.ProfileRepository;
import com.example.nexus.repository.SaleRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ProfileRepository profileRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void buyProduct(Long productId, HttpServletRequest servletRequest) {
        final var token = this.jwtService.getTokenFromRequest(servletRequest);
        final var username = this.jwtService.getUsernameFromToken(token);

        final var profile = this.profileRepository
                .findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException(MessageConstants.USER_NOT_FOUND));

        final var product = this.productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException(MessageConstants.PRODUCT_NOT_FOUND));

        if(product.getAvailability() == 0) {
            throw new NotFoundException(MessageConstants.PRODUCT_UNAVAILABLE);
        }

        final var actualPrice = product.getPrice() - product.getDiscount() * 0.01F * product.getPrice();

        if(profile.getBalance() < actualPrice) {
            throw new PaymentException(MessageConstants.NOT_ENOUGH_MONEY);
        }

        final var sale = new Sale();
        sale.setProduct(product);
        sale.setProfile(profile);
        sale.setPrice(actualPrice);
        sale.setSaleDate(LocalDate.now());

        product.setAvailability(product.getAvailability() - 1);
        profile.setBalance(profile.getBalance() - actualPrice);

        this.productRepository.save(product);
        this.profileRepository.save(profile);
        this.saleRepository.save(sale);
    }
}