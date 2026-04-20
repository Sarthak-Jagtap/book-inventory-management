package com.bookinventory.user.dto;

public class TokenValidationResponseDTO {

	private boolean valid;
	private String userName;
	private Integer userId;
	private String roleName;
	private String message;

	public TokenValidationResponseDTO() {
	}

	// Getters & Setters
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean v) {
		this.valid = v;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		this.userName = v;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer v) {
		this.userId = v;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String v) {
		this.roleName = v;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String v) {
		this.message = v;
	}
}