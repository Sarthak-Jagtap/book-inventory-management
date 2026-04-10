package com.bookinventory.user.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchaselog")
public class PurchaseLog {

    @EmbeddedId
    private PurchaseLogId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")                          
    @JoinColumn(name = "UserID", referencedColumnName = "UserID")
    private User user;

    // Constructors
    public PurchaseLog() {}

    public PurchaseLog(PurchaseLogId id, User user) {
        this.id   = id;
        this.user = user;
    }

    // Getters & Setters
    public PurchaseLogId getId() {
        return id;
    }

    public void setId(PurchaseLogId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Convenience getter for InventoryID (from the embedded composite key)
    public Integer getInventoryId() {
        return id != null ? id.getInventoryId() : null;
    }

    // toString
    @Override
    public String toString() {
        return "PurchaseLog{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUserId() : "null") +
                '}';
    }
}