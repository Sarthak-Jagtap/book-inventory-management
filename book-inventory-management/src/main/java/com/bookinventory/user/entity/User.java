package com.bookinventory.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "LastName", length = 30, nullable = false)
    private String lastName;

    @Column(name = "FirstName", length = 20, nullable = false)
    private String firstName;

    @Column(name = "PhoneNumber", length = 14)
    private String phoneNumber;

    @Column(name = "UserName", length = 30, nullable = false)
    private String userName;

    @Column(name = "Password", length = 30, nullable = false)
    private String password;

    // FK → permrole.RoleNumber  (default = 1 = Guest, handled at DB level)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RoleNumber", referencedColumnName = "RoleNumber")
    private PermRole role;

    // Constructors
    public User() {}

    public User(String lastName, String firstName, String phoneNumber,
                String userName, String password, PermRole role) {
        this.lastName    = lastName;
        this.firstName   = firstName;
        this.phoneNumber = phoneNumber;
        this.userName    = userName;
        this.password    = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PermRole getRole() {
        return role;
    }

    public void setRole(PermRole role) {
        this.role = role;
    }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", role=" + (role != null ? role.getPermRole() : "null") +
                '}';
    }
}