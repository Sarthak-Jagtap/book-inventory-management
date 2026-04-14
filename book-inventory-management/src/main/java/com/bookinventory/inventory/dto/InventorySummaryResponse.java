package com.bookinventory.inventory.dto;

import java.math.BigDecimal;

public class InventorySummaryResponse {

    private String isbn;
    private Integer rank;
    private String condition;
    private String fullDescription;
    private BigDecimal price;
    private Long availableCount;
    private Long totalCount;

    public InventorySummaryResponse() {
    }

    public InventorySummaryResponse(String isbn,
                                    Integer rank,
                                    String condition,
                                    String fullDescription,
                                    BigDecimal price,
                                    Long availableCount,
                                    Long totalCount) {
        this.isbn = isbn;
        this.rank = rank;
        this.condition = condition;
        this.fullDescription = fullDescription;
        this.price = price;
        this.availableCount = availableCount;
        this.totalCount = totalCount;
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

    public Long getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Long availableCount) {
        this.availableCount = availableCount;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}