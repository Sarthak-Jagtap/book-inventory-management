package com.bookinventory.cart.dto;

import java.math.BigDecimal;

public class CartViewResponse {

    private Integer userId;
    private String userName;
    private String isbn;
    private String bookTitle;
    private boolean selected;
    private Integer selectedRank;
    private String selectedCondition;
    private BigDecimal selectedPrice;

    public CartViewResponse() {
    }

    public CartViewResponse(Integer userId,
                            String userName,
                            String isbn,
                            String bookTitle,
                            boolean selected,
                            Integer selectedRank,
                            String selectedCondition,
                            BigDecimal selectedPrice) {
        this.userId = userId;
        this.userName = userName;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.selected = selected;
        this.selectedRank = selectedRank;
        this.selectedCondition = selectedCondition;
        this.selectedPrice = selectedPrice;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Integer getSelectedRank() {
        return selectedRank;
    }

    public void setSelectedRank(Integer selectedRank) {
        this.selectedRank = selectedRank;
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
}