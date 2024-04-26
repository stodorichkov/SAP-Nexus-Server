package com.example.nexus.service;

import com.example.nexus.model.payload.request.CampaignRequest;
import com.example.nexus.model.payload.response.CampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CampaignService {
    void startCampaign(String campaignName);
    void stopCampaign(String campaignName);
    void addCampaign(CampaignRequest campaignRequest);
    Page<CampaignResponse> getCampaigns(Pageable pageable);
    List<String> getCampaignsList();
    List<CampaignResponse> getActiveCampaigns();
    void editCampaign(String campaignName, CampaignRequest campaignRequest);
}