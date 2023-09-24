package com.gameloft.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "clans")
@Data
public class Clan {

    @Id
    private Long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clan")
    private List<Profile> members;

}
