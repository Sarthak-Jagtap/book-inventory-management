package com.bookinventory.bookreview.entity;

import com.bookinventory.book.entity.Book;
import com.bookinventory.reviewer.entity.Reviewer;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name="bookreview")
public class BookReview {

    @EmbeddedId
    private BookReviewId id;

    @ManyToOne
    @MapsId("isbn")   
    @JoinColumn(name="ISBN",columnDefinition = "CHAR(13)")
    private Book book;

    @ManyToOne
    @MapsId("reviewerId")  
    @JoinColumn(name="ReviewerID",columnDefinition = "int")
    private Reviewer reviewer;

    @Column(name="Rating")
    private int rating;

    @Column(name="Comments")
    private String comments;

	public BookReviewId getId() {
		return id;
	}

	public void setId(BookReviewId id) {
		this.id = id;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Reviewer getReviewer() {
		return reviewer;
	}

	public void setReviewer(Reviewer reviewer) {
		this.reviewer = reviewer;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
    
}