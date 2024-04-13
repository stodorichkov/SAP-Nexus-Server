package com.example.nexus.specification;

import com.example.nexus.constant.CampaignConstants;
import com.example.nexus.model.entity.Campaign;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class CampaignSpecifications {
    public static Specification<Campaign> findByActive(boolean isActive) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CampaignConstants.IS_ACTIVE), isActive);
    }

    public static Specification<Campaign> findByEndDate() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get(CampaignConstants.END_DATE), LocalDate.now());
    }

    public static Specification<Campaign> findByStartDate() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CampaignConstants.START_DATE), LocalDate.now());
    }
}