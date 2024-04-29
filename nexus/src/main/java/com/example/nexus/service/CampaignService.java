package com.example.nexus.service;

import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.payload.request.CampaignRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CampaignService {
    void startCampaign(String campaignName);
    void stopCampaign(String campaignName);
    void addCampaign(CampaignRequest campaignRequest);
    Page<Campaign> getCampaigns(Pageable pageable);
    List<String> getCampaignsList();
    List<String> getActiveCampaigns();
    void editCampaign(Long campaignId, CampaignRequest campaignRequest);
}