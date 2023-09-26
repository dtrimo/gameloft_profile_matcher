package com.gameloft.profilematcher.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

}
