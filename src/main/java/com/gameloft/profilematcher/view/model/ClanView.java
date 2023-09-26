package com.gameloft.profilematcher.view.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder(value = {"id", "name"})
public class ClanView {

    private String id;
    private String name;

}
