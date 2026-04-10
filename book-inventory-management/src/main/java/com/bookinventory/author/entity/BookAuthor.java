package com.bookinventory.author.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="bookauthor")
public class BookAuthor {

    @EmbeddedId
    private BookAuthorId id;

    @Column(name = "PrimaryAuthor", columnDefinition = "CHAR(1)")
    private String primaryAuthor;

	public BookAuthorId getId() {
		return id;
	}

	public void setId(BookAuthorId id) {
		this.id = id;
	}

	public String getPrimaryAuthor() {
		return primaryAuthor;
	}

	public void setPrimaryAuthor(String primaryAuthor) {
		this.primaryAuthor = primaryAuthor;
	}
}