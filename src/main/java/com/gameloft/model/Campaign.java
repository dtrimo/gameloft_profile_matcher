package com.gameloft.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "campaigns")
@Data
public class Campaign {

    private Long id;
    @ManyToOne
    private Game game;

    private Double priority;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private boolean enabled;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @PreUpdate
    public void setLastUpdatedTimestamp() {
        lastUpdated = new Date();
    }

}
