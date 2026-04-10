package com.bookinventory.user.dto;

public class LoginResponseDTO {

    private Integer userId;
    private String  userName;
    private String  firstName;
    private String  lastName;
    private String  roleName; 
    private String  message;    

    // Constructors
    public LoginResponseDTO() {}

    public LoginResponseDTO(Integer userId, String userName, String firstName,
                            String lastName, String roleName, String message) {
        this.userId    = userId;
        this.userName  = userName;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.roleName  = roleName;
        this.message   = message;
    }

    //Getters & Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}