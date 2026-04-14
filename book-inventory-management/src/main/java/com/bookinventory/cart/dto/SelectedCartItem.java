package com.bookinventory.cart.dto;

import java.math.BigDecimal;

public class SelectedCartItem {

    private Integer userId;
    private String isbn;
    private Integer inventoryId;
    private Integer rank;
    private BigDecimal price;

    public SelectedCartItem() {
    }

    public SelectedCartItem(Integer userId, String isbn, Integer inventoryId, Integer rank, BigDecimal price) {
        this.userId = userId;
        this.isbn = isbn;
        this.inventoryId = inventoryId;
        this.rank = rank;
        this.price = price;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}