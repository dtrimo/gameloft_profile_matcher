package com.gameloft.profilematcher.view.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameloft.profilematcher.model.Campaign;
import com.gameloft.profilematcher.view.model.CampaignView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CampaignMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public CampaignView map(Campaign campaign) {
        if (campaign == null) {
            return null;
        }
        CampaignView view = new CampaignView();
        view.setId(campaign.getId());
        if (campaign.getGame() != null) {
            view.setGame(campaign.getGame().getName());
        }
        try {
            view.setMatchers(objectMapper.readTree(campaign.getMatchers()));
        } catch (JsonProcessingException e) {
            log.warn("Could not read matchers json {}", campaign.getMatchers(), e);
        }
        view.setEnabled(campaign.isEnabled());
        view.setEndDate(campaign.getEndDate());
        view.setStartDate(campaign.getStartDate());
        view.setPriority(campaign.getPriority());
        view.setLastUpdated(campaign.getLastUpdated());
        view.setName(campaign.getName());
        return view;
    }

}
