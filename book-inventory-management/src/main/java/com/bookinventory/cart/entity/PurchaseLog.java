package com.bookinventory.cart.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "purchaselog")
@IdClass(PurchaseLogId.class)
public class PurchaseLog {

    @Id
    @Column(name = "UserID")
    private Integer userId;

    @Id
    @Column(name = "InventoryID")
    private Integer inventoryId;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }
}