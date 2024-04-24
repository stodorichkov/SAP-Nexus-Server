package com.example.nexus.repository;

import com.example.nexus.model.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {
    Optional<Campaign> findByName(String campaignName);
    List<Campaign> findByIsActive(boolean isActive);
}