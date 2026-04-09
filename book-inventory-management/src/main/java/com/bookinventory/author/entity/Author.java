package com.bookinventory.author.entity;

import jakarta.persistence.*;

@Entity
@Table(name="author")
public class Author {

    @Id
    @Column(name="authorID")
    private Integer authorID;

    @Column(name="LastName")
    private String lastName;

    @Column(name="FirstName")
    private String firstName;
    
    @Column(name="Photo")
    private String photo;
    
    
    public Author() {}
    public Author(Integer authorID, String firstName, String lastName, String photo) {
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}


    // getters setters
}
