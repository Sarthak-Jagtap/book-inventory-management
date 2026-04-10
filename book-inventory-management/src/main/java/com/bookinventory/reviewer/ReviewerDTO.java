package com.bookinventory.reviewer;


public class ReviewerDTO {
	
	private String name;
	private String employedBy;
	public ReviewerDTO(String name, String employedBy) {
		super();
		this.name = name;
		this.employedBy = employedBy;
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
