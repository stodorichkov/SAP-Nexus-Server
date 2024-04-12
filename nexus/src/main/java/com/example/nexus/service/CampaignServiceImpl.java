package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.repository.CampaignRepository;
import com.example.nexus.repository.ProductRepository;
import com.example.nexus.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void startCampaign(String campaignName) {
        final var campaign = this.campaignRepository.findByName(campaignName)
                .orElseThrow(() -> new NotFoundException(MessageConstants.CAMPAIGN_NOT_FOUND));
        final var specification = ProductSpecifications.findByCampaignName(campaignName);
        final var products = this.productRepository.findAll(specification);

        products.forEach(product ->
            product.setDiscount(product.getCampaignDiscount())
        );

        campaign.setIsActive(true);

        this.productRepository.saveAll(products);
        this.campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public void stopCampaign(String campaignName) {
        System.out.println(campaignName);

        final var campaign = this.campaignRepository.findByName(campaignName)
                .orElseThrow(() -> new NotFoundException(MessageConstants.CAMPAIGN_NOT_FOUND));
        final var specification = ProductSpecifications.findByCampaignName(campaignName);
        final var products = this.productRepository.findAll(specification);

        products.forEach(product -> {
            product.setCampaign(null);
            product.setCampaignDiscount(0);
            product.setDiscount(0);
        });

        campaign.setStartDate(null);
        campaign.setEndDate(null);
        campaign.setIsActive(false);

        this.productRepository.saveAll(products);
        this.campaignRepository.save(campaign);
    }
}