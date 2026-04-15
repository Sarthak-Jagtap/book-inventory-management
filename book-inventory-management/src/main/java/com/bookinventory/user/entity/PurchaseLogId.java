package com.bookinventory.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PurchaseLogId implements Serializable {
	@Column(name = "UserID")
	private Integer userId;

	@Column(name = "InventoryID")
	private Integer inventoryId;

	// Constructors
	public PurchaseLogId() {
	}

	public PurchaseLogId(Integer userId, Integer inventoryId) {
		this.userId = userId;
		this.inventoryId = inventoryId;
	}

	// Getters & Setters
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Integer inventoryId) {
		this.inventoryId = inventoryId;
	}

	// equals & hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PurchaseLogId))
			return false;
		PurchaseLogId that = (PurchaseLogId) o;
		return Objects.equals(userId, that.userId) && Objects.equals(inventoryId, that.inventoryId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, inventoryId);
	}

	@Override
	public String toString() {
		return "PurchaseLogId{userId=" + userId + ", inventoryId=" + inventoryId + '}';
	}
}