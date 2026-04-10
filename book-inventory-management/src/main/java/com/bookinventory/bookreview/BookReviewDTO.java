package com.bookinventory.bookreview;


public class BookReviewDTO {
	
	private int rating;
	private String comments;
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
	public BookReviewDTO(int rating, String comments) {
		super();
		this.rating = rating;
		this.comments = comments;
	}
	public BookReviewDTO() {
		
	}
	

}
