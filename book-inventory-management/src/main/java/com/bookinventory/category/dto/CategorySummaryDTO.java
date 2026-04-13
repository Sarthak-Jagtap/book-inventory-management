package com.bookinventory.category.dto;

public class CategorySummaryDTO {

    private String category;
    private Long bookCount;

    public CategorySummaryDTO(String category, Long bookCount) {
        this.category = category;
        this.bookCount = bookCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getBookCount() {
        return bookCount;
    }

    public void setBookCount(Long bookCount) {
        this.bookCount = bookCount;
    }
}