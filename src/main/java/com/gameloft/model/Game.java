package com.gameloft.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "games")
@Data
public class Game {

    @Id
    private Long id;
    private String name;
    private String releaseDate;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Campaign> campaigns;

}
