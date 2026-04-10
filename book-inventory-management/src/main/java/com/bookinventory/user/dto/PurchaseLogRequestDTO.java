package com.bookinventory.user.dto;

import jakarta.validation.constraints.NotNull;

public class PurchaseLogRequestDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Inventory ID is required")
    private Integer inventoryId;

    // Constructors
    public PurchaseLogRequestDTO() {}

    public PurchaseLogRequestDTO(Integer userId, Integer inventoryId) {
        this.userId      = userId;
        this.inventoryId = inventoryId;
    }

    // Getters & Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    @Override
    public String toString() {
        return "PurchaseLogRequestDTO{" +
                "userId=" + userId +
                ", inventoryId=" + inventoryId +
                '}';
    }
}