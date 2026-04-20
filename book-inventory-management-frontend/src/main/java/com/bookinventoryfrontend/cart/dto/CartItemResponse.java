package com.bookinventoryfrontend.cart.dto;

public class CartItemResponse {

    private Integer userId;
    private String isbn;

    public CartItemResponse() {
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