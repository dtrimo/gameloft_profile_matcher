package com.gameloft.profilematcher.view.controller;

import com.gameloft.profilematcher.repository.CampaignRepository;
import com.gameloft.profilematcher.view.mapper.CampaignMapper;
import com.gameloft.profilematcher.view.model.CampaignView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CampaignController {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignMapper campaignMapper;

    @RequestMapping(method = RequestMethod.GET, path = "/campaigns")
    @Transactional
    public ResponseEntity<List<CampaignView>> getCampaigns() {
        return ResponseEntity.ok(campaignRepository.findAll().stream().map(campaignMapper::map).collect(Collectors.toList()));
    }

}
