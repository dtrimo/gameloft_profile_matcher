package com.gameloft.profilematcher.view.controller;

import com.gameloft.profilematcher.service.ProfileService;
import com.gameloft.profilematcher.view.mapper.ProfileMapper;
import com.gameloft.profilematcher.view.model.ProfileView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileMapper profileMapper;

    @Transactional
    @RequestMapping(method = RequestMethod.GET, path = "/get_client_config/{player_id}")
    public ResponseEntity<ProfileView> getProfile(@PathVariable(name = "player_id") String playerId) {
        return profileService.getProfileByPlayerId(playerId)
                .map(profileMapper::map)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
