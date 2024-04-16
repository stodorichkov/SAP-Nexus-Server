package com.example.nexus.service;

import com.example.nexus.model.payload.request.CampaignRequest;

public interface CampaignService {
    void startCampaign(String campaignName);
    void stopCampaign(String campaignName);
    void addCampaign(CampaignRequest campaignRequest);
}