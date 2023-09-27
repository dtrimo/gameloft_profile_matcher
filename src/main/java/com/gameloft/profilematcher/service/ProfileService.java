package com.gameloft.profilematcher.service;

import com.gameloft.profilematcher.matchers.MatcherService;
import com.gameloft.profilematcher.model.Campaign;
import com.gameloft.profilematcher.model.Profile;
import com.gameloft.profilematcher.repository.CampaignRepository;
import com.gameloft.profilematcher.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MatcherService matcherService;

    @Transactional
    public Optional<Profile> getProfileByPlayerId(String playerId) {
        Optional<Profile> optionalProfile = profileRepository.findByPlayerId(playerId);
        optionalProfile.ifPresent(profile ->
                campaignRepository.findAll().stream()
                        .filter(Campaign::isEnabled)
                        .filter(campaign -> campaign.getStartDate().before(new Date()) && campaign.getEndDate().after(new Date()))
                        //.filter(campaign -> campaign.getName().equals("mycampaign4"))
                        .filter(campaign -> matcherService.matches(profile, campaign.getMatchers()))
                        .map(Campaign::getName)
                        .forEach(profile.getActiveCampaigns()::add));
        return optionalProfile;
    }

}
