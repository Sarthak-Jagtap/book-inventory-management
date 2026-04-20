package com.bookinventoryfrontend.author.dto;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;

public class AuthorDTO {

    private Integer authorID;
    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    private String photo;

    public AuthorDTO() {}


    public AuthorDTO(Integer authorID, String firstName, String lastName, String photo) {
        this.authorID = authorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
    }
    public Integer getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Integer authorID) {
        this.authorID = authorID;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}