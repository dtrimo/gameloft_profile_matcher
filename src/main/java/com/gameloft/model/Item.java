package com.gameloft.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    private Long id;

    private String name;

    private Long quantity;

    @ManyToOne
    private Profile profile;

}
