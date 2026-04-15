package com.bookinventory.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequestDTO {

	// All fields optional — only provided fields will be updated
	@Size(max = 30, message = "Last name must not exceed 30 characters")
	private String lastName;

	@Size(max = 20, message = "First name must not exceed 20 characters")
	private String firstName;

	@Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "Phone number must be in format (XXX) XXX-XXXX")
	private String phoneNumber;

	@Size(max = 30, message = "Username must not exceed 30 characters")
	private String userName;

	// Constructors
	public UserUpdateRequestDTO() {
	}

	public UserUpdateRequestDTO(String lastName, String firstName, String phoneNumber, String userName) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.phoneNumber = phoneNumber;
		this.userName = userName;
	}

	// Getters & Setters
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "UserUpdateRequestDTO{" + "lastName='" + lastName + '\'' + ", firstName='" + firstName + '\''
				+ ", phoneNumber='" + phoneNumber + '\'' + ", userName='" + userName + '\'' + '}';
	}
}
