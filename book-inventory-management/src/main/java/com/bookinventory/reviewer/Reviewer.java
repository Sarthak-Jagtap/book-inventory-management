package com.bookinventory.reviewer;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name="reviewer")
public class Reviewer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int ReviewerID;
	
	@NotBlank(message="Name cannot be empty")
	@Size(max=20 ,message="max characters can be 20")
	private String Name;
	
	@Column(name = "EmployedBy", columnDefinition = "CHAR(30)")
	private String employedBy;

	public int getReviewerID() {
		return ReviewerID;
	}

	public void setReviewerID(int reviewerID) {
		ReviewerID = reviewerID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getEmployedBy() {
		return employedBy;
	}

	public void setEmployedBy(String employedBy) {
		employedBy = employedBy;
	}

}
