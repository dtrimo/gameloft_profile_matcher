package com.gameloft.profilematcher.view.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Date;

@Data
@JsonPropertyOrder(value = {"name", "game", "priority", "matchers", "start_date", "end_date", "enabled", "last_updated"})
public class CampaignView {

    @JsonIgnore
    private Long id;

    private String name;

    private String game;

    private Double priority;

    @JsonProperty(value = "start_date")
    private Date startDate;

    @JsonProperty(value = "end_date")
    private Date endDate;

    private boolean enabled;

    @JsonProperty(value = "last_updated")
    private Date lastUpdated;

    private JsonNode matchers;

}
