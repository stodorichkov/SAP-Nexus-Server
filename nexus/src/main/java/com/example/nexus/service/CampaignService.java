package com.example.nexus.service;

import com.example.nexus.model.payload.request.StartCampaignRequest;

public interface CampaignService {
    void startCampaign(StartCampaignRequest startCampaignRequest);
    void stopCampaign(String campaignName);
}