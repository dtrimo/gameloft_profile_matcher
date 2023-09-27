package com.gameloft.profilematcher.view.mapper;

import com.gameloft.profilematcher.model.Item;
import com.gameloft.profilematcher.model.Profile;
import com.gameloft.profilematcher.view.model.ProfileView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProfileMapper {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private ClanMapper clanMapper;

    public ProfileView map(Profile profile) {
        if (profile == null) {
            return null;
        }
        ProfileView view = new ProfileView();
        view.setId(profile.getId());
        view.setClan(clanMapper.map(profile.getClan()));
        view.setCreated(profile.getCreated());
        view.setCountry(profile.getCountry());
        view.setDevices(Optional.ofNullable(profile.getDevices()).map(devices -> devices.stream().map(deviceMapper::map).collect(Collectors.toList()))
                .orElse(Collections.emptyList()));
        view.setGender(profile.getGender());
        view.setXp(profile.getXp());

        if (profile.getItems() != null) {
            view.setInventory(profile.getItems().stream().collect(Collectors.groupingBy(Item::getName, Collectors.summingLong(Item::getQuantity))));
        } else {
            view.setInventory(new HashMap<>());
        }

        view.setLanguage(profile.getLanguage());
        view.setBirthDate(profile.getBirthDate());
        view.setCustomField(profile.getCustomField());
        view.setLevel(profile.getLevel());
        view.setModified(profile.getModified());
        view.setPlayerId(profile.getPlayerId());
        view.setLastSession(profile.getLastSession());
        view.setTotalSpent(profile.getTotalSpent());
        view.setTotalRefund(profile.getTotalRefund());
        view.setTotalTransactions(profile.getTotalTransactions());
        view.setLastPurchase(profile.getLastPurchase());
        view.setTotalPlaytimeSeconds(profile.getTotalPlaytimeSeconds());
        view.setActiveCampaigns(new ArrayList<>(profile.getActiveCampaigns()));
        return view;
    }

}
