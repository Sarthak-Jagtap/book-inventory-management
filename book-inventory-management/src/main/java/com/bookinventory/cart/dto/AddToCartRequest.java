package com.bookinventory.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddToCartRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

   @NotBlank(message = "ISBN is required")
    @Pattern(
        regexp = "^[0-9\\-]{10,17}$",
        message = "ISBN must be valid, e.g. 1-295-84547-1"
    )
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
