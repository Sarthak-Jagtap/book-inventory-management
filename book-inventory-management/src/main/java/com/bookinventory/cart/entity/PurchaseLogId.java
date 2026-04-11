package com.bookinventory.cart.entity;

import java.io.Serializable;
import java.util.Objects;

public class PurchaseLogId implements Serializable {

    private Integer userId;
    private Integer inventoryId;

    public PurchaseLogId() {}

    public PurchaseLogId(Integer userId, Integer inventoryId) {
        this.userId = userId;
        this.inventoryId = inventoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseLogId)) return false;
        PurchaseLogId that = (PurchaseLogId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(inventoryId, that.inventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, inventoryId);
    }
}