package com.bookinventoryfrontend.dto;

public class LoginRequestDTO {
	private String userName;
	private String password;

	public LoginRequestDTO() {
	}

	public LoginRequestDTO(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		this.userName = v;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String v) {
		this.password = v;
	}
}
