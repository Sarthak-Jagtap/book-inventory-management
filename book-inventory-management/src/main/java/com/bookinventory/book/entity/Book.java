package com.bookinventory.book.entity;

import com.bookinventory.category.entity.Category;
import com.bookinventory.publisher.entity.Publisher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
public class Book {

	@Id
	@Column(name = "ISBN", columnDefinition = "CHAR(13)", nullable = false)
	private String isbn;

	@Column(name = "Title", nullable = false)
	private String title;

	@Column(name = "Description")
	private String description;

	@Column(name = "Edition", columnDefinition = "CHAR(30)")
	private String edition;

	@ManyToOne
	@JoinColumn(name = "Category")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "PublisherID", nullable = false)
	private Publisher publisher;

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

}
