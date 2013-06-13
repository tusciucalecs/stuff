package com.bbob.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "account", uniqueConstraints =
    @UniqueConstraint(columnNames = { "username" }))
public class Account extends BaseEntity {

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(cascade = { CascadeType.ALL })
    private User user;

    public Account() {
        super();
    }

    public Account(String username, String password, String name) {
        super();
        this.username = username;
        this.password = password;
        this.user = new User(name, Role.ROLE_USER);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
