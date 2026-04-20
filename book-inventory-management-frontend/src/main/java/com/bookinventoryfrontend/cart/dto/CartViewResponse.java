package com.bookinventoryfrontend.cart.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartViewResponse {

    private Integer userId;
    private String isbn;
    private String bookTitle;
    private String selectedCondition;
    private BigDecimal selectedPrice;
    private List<CartOptionResponse> qualityOptions = new ArrayList<>();

    public CartViewResponse() {
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

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getSelectedCondition() {
        return selectedCondition;
    }

    public void setSelectedCondition(String selectedCondition) {
        this.selectedCondition = selectedCondition;
    }

    public BigDecimal getSelectedPrice() {
        return selectedPrice;
    }

    public void setSelectedPrice(BigDecimal selectedPrice) {
        this.selectedPrice = selectedPrice;
    }

    public List<CartOptionResponse> getQualityOptions() {
        return qualityOptions;
    }

    public void setQualityOptions(List<CartOptionResponse> qualityOptions) {
        this.qualityOptions = qualityOptions;
    }
}