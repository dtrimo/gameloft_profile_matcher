package com.gameloft.profilematcher.view.controller;

import com.gameloft.profilematcher.matchers.MatcherService;
import com.gameloft.profilematcher.model.Campaign;
import com.gameloft.profilematcher.model.Profile;
import com.gameloft.profilematcher.repository.CampaignRepository;
import com.gameloft.profilematcher.view.mapper.ProfileMapper;
import com.gameloft.profilematcher.view.model.ProfileView;
import com.gameloft.profilematcher.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MatcherService matcherService;

    @RequestMapping(method = RequestMethod.GET, path = "/get_client_config/{player_id}")
    public ResponseEntity<ProfileView> getProfile(@PathVariable(name = "player_id") String playerId) {
        Optional<Profile> optionalProfile = profileRepository.findByPlayerId(playerId);
        List<String> activeCampaigns = new ArrayList<>();
        optionalProfile.ifPresent(profile ->
                campaignRepository.findAll().stream()
                        .filter(campaign -> matcherService.matches(profile, campaign.getMatchers()))
                        .map(Campaign::getName)
                        .forEach(activeCampaigns::add));
        return optionalProfile
                .map(profileMapper::map)
                .map(profileView -> {
                    profileView.setActiveCampaigns(activeCampaigns);
                    return profileView;
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
