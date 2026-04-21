package com.bookinventoryfrontend.inventory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateInventoryRequest {

    @NotNull(message = "Rank is required")
    @Min(value = 1, message = "Rank must be at least 1")
    @Max(value = 6, message = "Rank must be at most 6")
    private Integer rank;

    private Boolean purchased;

    public UpdateInventoryRequest() {
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