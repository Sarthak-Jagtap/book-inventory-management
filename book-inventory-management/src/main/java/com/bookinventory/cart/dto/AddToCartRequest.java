package com.bookinventory.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddToCartRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "ISBN is required")
    @Size(min = 13, max = 13, message = "ISBN must be exactly 13 characters")
    @Pattern(regexp = "^[0-9\\-]{13}$", message = "Invalid ISBN format")
    private String isbn;

    public AddToCartRequest() {
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