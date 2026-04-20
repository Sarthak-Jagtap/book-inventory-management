package com.bookinventoryfrontend.cart.dto;

public class CheckoutItemRequest {

    private String isbn;
    private Integer rank;

    public CheckoutItemRequest() {
    }

    public CheckoutItemRequest(String isbn, Integer rank) {
        this.isbn = isbn;
        this.rank = rank;
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
}