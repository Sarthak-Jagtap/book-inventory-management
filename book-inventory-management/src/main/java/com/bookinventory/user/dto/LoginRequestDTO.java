package com.bookinventory.user.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public LoginRequestDTO() {}

    public LoginRequestDTO(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Getters & Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequestDTO{userName='" + userName + "', password='[PROTECTED]'}";
    }
}
