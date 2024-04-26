package com.example.nexus.service;

import com.example.nexus.constant.MessageConstants;
import com.example.nexus.exception.AlreadyExistsException;
import com.example.nexus.exception.NotFoundException;
import com.example.nexus.mapper.CampaignMapper;
import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.payload.request.CampaignRequest;
import com.example.nexus.model.payload.response.CampaignResponse;
import com.example.nexus.repository.CampaignRepository;
import com.example.nexus.repository.ProductRepository;
import com.example.nexus.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;
    private final CampaignMapper campaignMapper;

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

    @Override
    public void addCampaign(CampaignRequest campaignRequest) {
        if (this.campaignRepository.findByName(campaignRequest.name()).isPresent()) {
            throw new AlreadyExistsException(MessageConstants.CAMPAIGN_EXISTS);
        }

        final var newCampaign = this.campaignMapper.campaignRequestToCampaign(campaignRequest);

        campaignRepository.save(newCampaign);
    }

    @Override
    public Page<CampaignResponse> getCampaigns(Pageable pageable) {
        return this.campaignRepository
                .findAll(pageable)
                .map(this.campaignMapper::campaignToCampaignResponse);
    }

    @Override
    public List<String> getCampaignsList() {
        return this.campaignRepository
                .findAll().stream().map(Campaign::getName)
                .toList();
    }

    @Override
    public List<CampaignResponse> getActiveCampaigns() {
        final var activeCampaigns = this.campaignRepository.findByIsActive(true);
        return activeCampaigns.stream()
                .map(this.campaignMapper::campaignToCampaignResponse)
                .toList();
    }

    @Override
    public void editCampaign(String campaignName, CampaignRequest campaignRequest) {
        final var campaign = this.campaignRepository.findByName(campaignName)
                .orElseThrow(() -> new NotFoundException(MessageConstants.CAMPAIGN_NOT_FOUND));

        if (!campaignRequest.isStartDateBeforeEndDate()) {
            throw new IllegalArgumentException(MessageConstants.INVALID_END_DATE);
        }

        campaign.setName(campaignRequest.name());
        campaign.setStartDate(campaignRequest.startDate());
        campaign.setEndDate(campaignRequest.endDate());

        this.campaignRepository.save(campaign);
    }
}