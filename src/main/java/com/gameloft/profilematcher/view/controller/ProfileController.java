package com.gameloft.profilematcher.view.controller;

import com.gameloft.profilematcher.view.mapper.ProfileMapper;
import com.gameloft.profilematcher.view.model.ProfileView;
import com.gameloft.profilematcher.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;

    @RequestMapping(method = RequestMethod.GET, path = "/get_client_config/{player_id}")
    public ResponseEntity<ProfileView> getProfile(@PathVariable(name = "player_id") String playerId) {
        return profileRepository.findByPlayerId(playerId)
                .map(profileMapper::map)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
