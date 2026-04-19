package com.bookinventoryfrontend.dto;

public class BookDTO {

    private String isbn;
    private String title;
    private String description;
    private Integer categoryId;
    private Integer publisherId;
    private String edition;

    public BookDTO() {}

    public BookDTO(String isbn, String title, String description,
                   Integer categoryId, Integer publisherId, String edition) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.publisherId = publisherId;
        this.edition = edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }
}