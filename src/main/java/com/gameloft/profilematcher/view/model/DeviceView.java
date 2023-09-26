package com.gameloft.profilematcher.view.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder(value = {"id", "model", "carrier", "firmware"})
public class DeviceView {

    private Long id;
    private String model;
    private String carrier;
    private String firmware;

}
