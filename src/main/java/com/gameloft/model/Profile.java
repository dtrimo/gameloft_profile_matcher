package com.gameloft.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "profiles")
@Data
public class Profile {

    @Id
    private Long id;

    private String playerId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSession;

    private Double totalSpent;

    private Double totalRefund;

    private Integer totalTransactions;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPurchase;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile", orphanRemoval = true)
    private List<Device> devices;

    private Integer level;

    private Integer xp;

    private Long totalPlaytimeSeconds;

    private String country; //can also be an enum

    private String language;    //can also be an enum

    private Date birthDate;

    private String gender;  //can also be an enum

    @ManyToOne
    private Clan clan;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profile")
    private List<Item> items;

    private String customField;

}
