package com.bookinventory.cart.dto;

import java.util.ArrayList;
import java.util.List;

public class CartViewResponse {

    private Integer userId;
    private String userName;
    private String isbn;
    private String bookTitle;
    private List<CartOptionResponse> qualityOptions = new ArrayList<>();

    public CartViewResponse() {
    }

    public CartViewResponse(Integer userId, String userName, String isbn, String bookTitle,
                            List<CartOptionResponse> qualityOptions) {
        this.userId = userId;
        this.userName = userName;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.qualityOptions = qualityOptions;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public List<CartOptionResponse> getQualityOptions() {
        return qualityOptions;
    }

    public void setQualityOptions(List<CartOptionResponse> qualityOptions) {
        this.qualityOptions = qualityOptions;
    }
}