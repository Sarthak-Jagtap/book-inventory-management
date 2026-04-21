package com.bookinventoryfrontend.inventory.dto;

import java.math.BigDecimal;

public class BookConditionResponse {
    private Integer rank;
    private String description;
    private String fullDescription;
    private BigDecimal price;

    public BookConditionResponse() {
    }

    public BookConditionResponse(Integer rank, String description, String fullDescription, BigDecimal price) {
        this.rank = rank;
        this.description = description;
        this.fullDescription = fullDescription;
        this.price = price;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
