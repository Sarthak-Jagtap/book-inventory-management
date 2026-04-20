package com.bookinventoryfrontend.dto;

public class CategorySummaryDTO {

    private String category;
    private Long bookCount;

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