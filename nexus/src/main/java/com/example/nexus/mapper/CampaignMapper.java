package com.example.nexus.mapper;

import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.payload.request.CampaignRequest;
import com.example.nexus.model.payload.response.CampaignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CampaignMapper {
    Campaign campaignRequestToCampaign(CampaignRequest campaignRequest);
    CampaignResponse campaignToCampaignResponse(Campaign campaign);
    void updateCampaignFromRequest(CampaignRequest campaignRequest, @MappingTarget Campaign campaign);
}