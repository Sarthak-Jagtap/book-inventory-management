package com.bookinventory.reviewer;


public class ReviewerDTO {
	
	private String Name;
	private String EmployedBy;
	public ReviewerDTO(String name, String employedBy) {
		super();
		Name = name;
		EmployedBy = employedBy;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getEmployedBy() {
		return EmployedBy;
	}
	public void setEmployedBy(String employedBy) {
		EmployedBy = employedBy;
	}
	
	public ReviewerDTO() {
		
	}

}
