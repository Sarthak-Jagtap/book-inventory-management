package com.bookinventory.user.dto;

public class TopBuyerDTO {

	private Integer userId;
	private String userName;
	private String firstName;
	private String lastName;
	private long purchaseCount;

	public TopBuyerDTO() {
	}

	public TopBuyerDTO(Integer userId, String userName, String firstName, String lastName, long purchaseCount) {
		this.userId = userId;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.purchaseCount = purchaseCount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer v) {
		this.userId = v;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		this.userName = v;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String v) {
		this.firstName = v;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String v) {
		this.lastName = v;
	}

	public long getPurchaseCount() {
		return purchaseCount;
	}

	public void setPurchaseCount(long v) {
		this.purchaseCount = v;
	}
}