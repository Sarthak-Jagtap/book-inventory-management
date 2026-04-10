package com.bookinventory.user.dto;

public class UserResponseDTO {

    private Integer userId;
    private String  lastName;
    private String  firstName;
    private String  phoneNumber;
    private String  userName;

    // We return role info as a nested DTO — not raw roleNumber
    private PermRoleResponseDTO role;

    // Constructors
    public UserResponseDTO() {}

    public UserResponseDTO(Integer userId, String lastName, String firstName,
                           String phoneNumber, String userName,
                           PermRoleResponseDTO role) {
        this.userId      = userId;
        this.lastName    = lastName;
        this.firstName   = firstName;
        this.phoneNumber = phoneNumber;
        this.userName    = userName;
        this.role        = role;
    }

    // Getters & Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public PermRoleResponseDTO getRole() {
        return role;
    }

    public void setRole(PermRoleResponseDTO role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "userId=" + userId +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                '}';
    }
}