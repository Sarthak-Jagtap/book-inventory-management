package com.bookinventory.author.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BookAuthorId implements Serializable {
	
    @Column(name = "ISBN", columnDefinition = "CHAR(13)")
    private String ISBN;

	@Column(name = "AuthorID")
	    private Integer authorID;

    @Override
	public int hashCode() {
		return Objects.hash(ISBN, authorID);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookAuthorId other = (BookAuthorId) obj;
		return Objects.equals(ISBN, other.ISBN) && Objects.equals(authorID, other.authorID);
	}
	public String getISBN() {
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	public Integer getAuthorID() {
		return authorID;
	}
	public void setAuthorID(Integer authorID) {
		this.authorID = authorID;
	}

    // getters setters equals hashcode
}
