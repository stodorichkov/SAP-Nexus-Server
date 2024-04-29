package com.example.nexus.mapper;

import com.example.nexus.model.entity.Campaign;
import com.example.nexus.model.payload.request.CampaignRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CampaignMapper {
    Campaign campaignRequestToCampaign(CampaignRequest campaignRequest);
    void updateCampaign(CampaignRequest campaignRequest, @MappingTarget Campaign campaign);
}