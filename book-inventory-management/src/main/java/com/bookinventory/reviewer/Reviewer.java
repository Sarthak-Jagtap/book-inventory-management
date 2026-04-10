package com.bookinventory.reviewer;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name="reviewer")
public class Reviewer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ReviewerID")
	private int reviewerID;
	
	@NotBlank(message="Name cannot be empty")
	@Size(max=20 ,message="max characters can be 20")
	@Column(name="Name")
	private String name;
	
	@Column(name = "EmployedBy", columnDefinition = "CHAR(30)")
	private String employedBy;

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

}
