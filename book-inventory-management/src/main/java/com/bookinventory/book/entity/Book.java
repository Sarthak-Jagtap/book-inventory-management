package com.bookinventory.book.entity;

import com.bookinventory.category.entity.Category;
import com.bookinventory.publisher.entity.Publisher;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @NotBlank
    @Size(max = 13)
    @Column(name = "ISBN", columnDefinition = "CHAR(13)", nullable = false)
    private String isbn;

    @NotBlank
    @Size(max = 70)
    @Column(name = "Title", length = 70, nullable = false)
    private String title;

    @Size(max = 100)
    @Column(name = "Description", length = 100)
    private String description;

    @Size(max = 30)
    @Column(name = "Edition", columnDefinition = "CHAR(30)")
    private String edition;

    @ManyToOne(optional = true)
    @JoinColumn(name = "Category")
    private Category category;

    @ManyToOne(optional = false)
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
