package com.gameloft.profilematcher.view.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonPropertyOrder(value = {"player_id", "created", "modified", "last_session", "total_spent", "total_refund",
        "total_transactions", "last_purchase", "active_campaigns", "devices", "level", "xp", "total_playtime",
        "country", "language", "birth_date", "gender", "inventory", "clan", "_customfield"})
public class ProfileView {

    @JsonIgnore
    private Long id;

    @JsonProperty(value = "player_id")
    private String playerId;

    private Date created;

    private Date modified;

    @JsonProperty(value = "last_session")
    private Date lastSession;

    @JsonProperty(value = "total_spent")
    private Double totalSpent;

    @JsonProperty(value = "total_refund")
    private Double totalRefund;

    @JsonProperty(value = "total_transactions")
    private Integer totalTransactions;

    @JsonProperty(value = "last_purchase")
    private Date lastPurchase;

    @JsonProperty(value = "active_campaigns")
    private List<String> activeCampaigns = new ArrayList<>();

    private List<DeviceView> devices;

    private Integer level;

    private Integer xp;

    @JsonProperty(value = "total_playtime")
    private Long totalPlaytimeSeconds;

    private String country; //can also be an enum

    private String language;    //can also be an enum

    @JsonProperty(value = "birth_date")
    private Date birthDate;

    private String gender;  //can also be an enum

    private ClanView clan;

    private Map<String, Long> inventory;

    @JsonProperty(value = "_customfield")
    private String customField;

}
