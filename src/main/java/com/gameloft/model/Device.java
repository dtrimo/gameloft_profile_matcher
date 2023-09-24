package com.gameloft.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "devices")
@Data
public class Device {

    @Id
    private Long id;

    private String model;

    private String carrier;     //can also be an enum

    private String firmware;

    @ManyToOne
    private Profile profile;

}
