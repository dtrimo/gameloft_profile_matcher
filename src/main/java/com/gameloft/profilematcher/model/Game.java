package com.gameloft.profilematcher.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "games")
@Data
public class Game {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "release_date")
    private Date releaseDate;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Campaign> campaigns;

}
