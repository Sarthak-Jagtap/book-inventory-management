package com.bookinventory.cart.dto;

public class CartItemResponse {

    private Integer userId;
    private String isbn;

    public CartItemResponse() {
    }

    public CartItemResponse(Integer userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
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
}