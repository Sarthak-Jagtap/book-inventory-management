package com.bookinventory.inventory.entity;

import java.io.Serializable;
import java.util.Objects;

public class ShoppingCartId implements Serializable {

    private Integer userId;
    private String isbn;

    public ShoppingCartId() {}

    public ShoppingCartId(Integer userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
    }

    // equals + hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoppingCartId)) return false;
        ShoppingCartId that = (ShoppingCartId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, isbn);
    }
}