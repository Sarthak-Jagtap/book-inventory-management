package com.bookinventory.cart.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class AddToCartRequest {

	@NotNull(message = "User ID is required")
	private Integer userId;

	@NotBlank(message = "ISBN is required")
	private String isbn;

	@NotNull(message = "Rank is required")
	@Min(value = 1, message = "Rank must be at least 1")
	@Max(value = 6, message = "Rank must be at most 6")
	private Integer rank;

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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}