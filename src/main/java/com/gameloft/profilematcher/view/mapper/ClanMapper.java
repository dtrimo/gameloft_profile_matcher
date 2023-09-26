package com.gameloft.profilematcher.view.mapper;

import com.gameloft.profilematcher.model.Clan;
import com.gameloft.profilematcher.view.model.ClanView;
import org.springframework.stereotype.Component;

@Component
public class ClanMapper {

    public ClanView map(Clan clan) {
        if (clan == null) {
            return null;
        }
        ClanView view = new ClanView();
        view.setId(String.valueOf(clan.getId()));
        view.setName(clan.getName());
        return view;
    }

}
