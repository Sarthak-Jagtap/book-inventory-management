package com.bookinventory.inventory.dto;

public class UpdateInventoryRequest {

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