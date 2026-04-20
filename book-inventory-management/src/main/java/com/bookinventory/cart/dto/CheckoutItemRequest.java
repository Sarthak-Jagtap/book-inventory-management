package com.bookinventory.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutItemRequest {

    @NotBlank(message = "ISBN is required")
    @Size(min = 13, max = 13, message = "ISBN must be exactly 13 characters")
    @Pattern(regexp = "^[0-9\\-]{13}$", message = "Invalid ISBN format")
    private String isbn;

    @NotNull(message = "Rank is required")
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