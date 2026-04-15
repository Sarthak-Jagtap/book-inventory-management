package com.bookinventory.bookreview.entity;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class BookReviewId implements Serializable {

    private String isbn;
    private Integer reviewerId;

    public BookReviewId() {}

    public BookReviewId(String isbn, Integer reviewerId) {
        this.isbn = isbn;
        this.reviewerId = reviewerId;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookReviewId)) return false;
        BookReviewId that = (BookReviewId) o;
        return Objects.equals(isbn, that.isbn) &&
               Objects.equals(reviewerId, that.reviewerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, reviewerId);
    }
}