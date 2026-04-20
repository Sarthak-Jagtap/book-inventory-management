package com.bookinventoryfrontend.inventory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class InventoryRequest {

    @NotBlank(message = "ISBN is required")
    @Pattern(
        regexp = "^[0-9\\-]{10,17}$",
        message = "ISBN must be valid, e.g. 1-295-84547-1"
    )
    private String isbn;

    @NotNull(message = "Rank is required")
    @Min(value = 1, message = "Rank must be at least 1")
    @Max(value = 6, message = "Rank must be at most 6")
    private Integer rank;

    // New items default to 'available' (not purchased)
    private Boolean purchased = false;

    public InventoryRequest() {}

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

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }
}