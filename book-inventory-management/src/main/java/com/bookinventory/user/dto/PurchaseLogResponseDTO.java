package com.bookinventory.user.dto;

public class PurchaseLogResponseDTO {

    private Integer userId;
    private Integer inventoryId;

    private String  userFirstName;
    private String  userLastName;
    private String  userName;

    // Constructors
    public PurchaseLogResponseDTO() {}

    public PurchaseLogResponseDTO(Integer userId, Integer inventoryId,
                                  String userFirstName, String userLastName,
                                  String userName) {
        this.userId        = userId;
        this.inventoryId   = inventoryId;
        this.userFirstName = userFirstName;
        this.userLastName  = userLastName;
        this.userName      = userName;
    }

    // Getters & Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "PurchaseLogResponseDTO{" +
                "userId=" + userId +
                ", inventoryId=" + inventoryId +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}