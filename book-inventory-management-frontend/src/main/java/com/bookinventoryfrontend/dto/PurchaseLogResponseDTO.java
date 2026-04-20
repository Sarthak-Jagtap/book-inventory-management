package com.bookinventoryfrontend.dto;

public class PurchaseLogResponseDTO {
	private Integer userId;
	private Integer inventoryId;
	private String userFirstName;
	private String userLastName;
	private String userName;

	public PurchaseLogResponseDTO() {
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer v) {
		this.userId = v;
	}

	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Integer v) {
		this.inventoryId = v;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String v) {
		this.userFirstName = v;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String v) {
		this.userLastName = v;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		this.userName = v;
	}

	public String getFullName() {
		return (userFirstName != null ? userFirstName : "") + " " + (userLastName != null ? userLastName : "");
	}
}
