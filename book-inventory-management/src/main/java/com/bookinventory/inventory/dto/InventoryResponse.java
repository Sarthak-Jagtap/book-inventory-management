package com.bookinventory.inventory.dto;

import java.math.BigDecimal;

public class InventoryResponse {

    private Integer inventoryId;
    private String isbn;
    private Integer rank;
    private Boolean purchased;
    private String condition;
    private String fullDescription;
    private BigDecimal price;

    public InventoryResponse() {
    }

    public InventoryResponse(Integer inventoryId,
                             String isbn,
                             Integer rank,
                             Boolean purchased,
                             String condition,
                             String fullDescription,
                             BigDecimal price) {
        this.inventoryId = inventoryId;
        this.isbn = isbn;
        this.rank = rank;
        this.purchased = purchased;
        this.condition = condition;
        this.fullDescription = fullDescription;
        this.price = price;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}