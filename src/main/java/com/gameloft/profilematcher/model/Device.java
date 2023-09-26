package com.gameloft.profilematcher.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "devices")
@Data
public class Device {

    @Id
    @GeneratedValue
    private Long id;

    private String model;

    private String carrier;     //can also be an enum

    private String firmware;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

}
