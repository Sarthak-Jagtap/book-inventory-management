package com.bookinventory.author.dto;


public class AuthorDTO {

    private Integer authorID;
    private String firstName;
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