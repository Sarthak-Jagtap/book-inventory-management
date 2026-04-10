package com.bookinventory.bookreview;

import com.bookinventory.reviewer.Reviewer;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name="bookreview")
public class BookReview {
	
	//@ManyToOne
	//@JoinColumn(name="ISBN",referencedColumnName="ISBN")
	//private Book book;
	
	@Id
	@ManyToOne
	@JoinColumn(name="ReviewerID",referencedColumnName="ReviewerID")
	private Reviewer reviewer;
	
	
	@NotNull(message="Rating Required")
	@Min(value=1,message="Minimum Rating is 1")
	@Max(value=10,message="Maximum rating is 10")
	private int Rating;
	
	@Size(max=225,message="Comment to long")
	private String Comments;

	//public Book getBook() {
		//return book;
	//}

	//public void setBook(Book book) {
		//this.book = book;
	//}

	public Reviewer getReviewer() {
		return reviewer;
	}

	public void setReviewer(Reviewer reviewer) {
		this.reviewer = reviewer;
	}

	public int getRating() {
		return Rating;
	}

	public void setRating(int rating) {
		Rating = rating;
	}

	public String getComments() {
		return Comments;
	}

	public void setComments(String comments) {
		Comments = comments;
	}
	
	

}
