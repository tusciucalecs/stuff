package com.bbob.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "experiment")
public class Experiment extends BaseEntity {

    @Column(name = "date", nullable = false)
    private Date date;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne
    private User user;

    public Experiment() {
        super();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
