package com.bookinventory.inventory.dto;

import java.math.BigDecimal;

public class CartOptionResponse {

    private String isbn;
    private Integer rank;
    private String condition;
    private BigDecimal price;
    private Long availableCount;

    public CartOptionResponse() {
    }

    public CartOptionResponse(String isbn, Integer rank, String condition, BigDecimal price, Long availableCount) {
        this.isbn = isbn;
        this.rank = rank;
        this.condition = condition;
        this.price = price;
        this.availableCount = availableCount;
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Long availableCount) {
        this.availableCount = availableCount;
    }
}