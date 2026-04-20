package com.bookinventoryfrontend.bookreview.dto;

public class BookReviewDTO {

    private String isbn;
    private int reviewerId;
    private int rating;
    private String comments;

    // 🔹 Default Constructor
    public BookReviewDTO() {
    }

    // 🔹 Parameterized Constructor
    public BookReviewDTO(String isbn, int reviewerId, int rating, String comments) {
        this.isbn = isbn;
        this.reviewerId = reviewerId;
        this.rating = rating;
        this.comments = comments;
    }

    // 🔹 Getters & Setters

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}