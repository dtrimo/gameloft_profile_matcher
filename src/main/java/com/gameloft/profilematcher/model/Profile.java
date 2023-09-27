package com.gameloft.profilematcher.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "player_profiles")
@Data
public class Profile {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "player_id", length = 40)
    private String playerId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_session")
    private Date lastSession;

    @Column(name = "total_spent")
    private Double totalSpent;

    @Column(name = "total_refund")
    private Double totalRefund;

    @Column(name = "total_transactions")
    private Integer totalTransactions;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_purchase")
    private Date lastPurchase;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile", orphanRemoval = true)
    private List<Device> devices;

    private Integer level;

    private Integer xp;

    @Column(name = "total_playtime_seconds")
    private Long totalPlaytimeSeconds;

    private String country; //can also be an enum

    private String language;    //can also be an enum

    @Column(name = "birth_date")
    private Date birthDate;

    private String gender;  //can also be an enum

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id")
    private Clan clan;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "profile")
    private List<Item> items;

    @Column(name = "custom_field")
    private String customField;

    //This field is added here for convenience and is not persisted
    @Transient
    private List<String> activeCampaigns = new ArrayList<>();

    @PrePersist
    public void setCreatedTimestamp() {
        if (created == null) {
            created = new Date();
        }
    }

    @PreUpdate
    public void setModifiedTimestamp() {
        modified = new Date();
    }
}
