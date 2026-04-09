package com.bookinventory.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shoppingcart")
@IdClass(ShoppingCartId.class)
public class ShoppingCart {

    @Id
    @Column(name = "UserID")
    private Integer userId;

    @Id
    @Column(name = "ISBN")
    private String isbn;

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