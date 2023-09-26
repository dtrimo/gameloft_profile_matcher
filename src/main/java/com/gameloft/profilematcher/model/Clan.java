package com.gameloft.profilematcher.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "clans")
@Data
public class Clan {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clan", orphanRemoval = false)
    private List<Profile> members;

}
