package com.gameloft.profilematcher.repository;

import com.gameloft.profilematcher.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
