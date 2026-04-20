package com.bookinventoryfrontend.reviewer.dto;


public class ReviewerDTO {
	
	private int reviewerID;
	private String name;
	private String employedBy;
	public ReviewerDTO(int reviewerID,String name, String employedBy) {
		super();
		this.reviewerID= reviewerID;
		this.name = name;
		this.employedBy = employedBy;
	}
	public int getReviewerID() {
		return reviewerID;
	}
	public void setReviewerID(int reviewerID) {
		this.reviewerID = reviewerID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmployedBy() {
		return employedBy;
	}
	public void setEmployedBy(String employedBy) {
		this.employedBy = employedBy;
	}
	
	public ReviewerDTO() {
		
	}

}
