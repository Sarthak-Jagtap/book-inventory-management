package com.bookinventory.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CheckoutRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @Valid
    private List<CheckoutItemRequest> items = new ArrayList<>();

    public CheckoutRequest() {
    }

    public CheckoutRequest(Integer userId, List<CheckoutItemRequest> items) {
        this.userId = userId;
        this.items = items;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<CheckoutItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItemRequest> items) {
        this.items = items;
    }
}