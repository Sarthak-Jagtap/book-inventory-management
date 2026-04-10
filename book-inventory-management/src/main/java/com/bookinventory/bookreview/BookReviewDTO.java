package com.bookinventory.bookreview;


public class BookReviewDTO {
	
	private int Rating;
	private String Comments;
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
	public BookReviewDTO(int rating, String comments) {
		super();
		Rating = rating;
		Comments = comments;
	}
	public BookReviewDTO() {
		
	}
	

}
