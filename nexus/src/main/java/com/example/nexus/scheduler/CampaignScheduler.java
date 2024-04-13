package com.example.nexus.scheduler;

import com.example.nexus.model.entity.Campaign;
import com.example.nexus.repository.CampaignRepository;
import com.example.nexus.service.CampaignService;
import com.example.nexus.specification.CampaignSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignScheduler implements InitializingBean {
    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;

    @Scheduled(cron = "0 5 1 * * ?")
    public void stopCampaigns() {
        final var specification = CampaignSpecifications.findByActive(true)
                .and(CampaignSpecifications.findByEndDate());

        this.campaignRepository
                .findAll(specification)
                .stream()
                .map(Campaign::getName)
                .forEach(this.campaignService::stopCampaign);
    }

    @Scheduled(cron = "0 10 1 * * ?")
    public void startCampaigns() {
        final var specification = CampaignSpecifications.findByActive(false)
                .and(CampaignSpecifications.findByStartDate());

        this.campaignRepository
                .findAll(specification)
                .stream()
                .map(Campaign::getName)
                .forEach(this.campaignService::startCampaign);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        stopCampaigns();
        startCampaigns();
    }
}