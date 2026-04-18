package com.bookinventory.user.dto;

public class PurchaseStatsDTO {

	private long totalPurchases; // Total rows in purchaselog table
	private long uniqueBuyers; // How many distinct users have purchased
	private long uniqueItemsSold; // How many distinct inventory items sold

	public PurchaseStatsDTO() {
	}

	public PurchaseStatsDTO(long totalPurchases, long uniqueBuyers, long uniqueItemsSold) {
		this.totalPurchases = totalPurchases;
		this.uniqueBuyers = uniqueBuyers;
		this.uniqueItemsSold = uniqueItemsSold;
	}

	public long getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(long v) {
		this.totalPurchases = v;
	}

	public long getUniqueBuyers() {
		return uniqueBuyers;
	}

	public void setUniqueBuyers(long v) {
		this.uniqueBuyers = v;
	}

	public long getUniqueItemsSold() {
		return uniqueItemsSold;
	}

	public void setUniqueItemsSold(long v) {
		this.uniqueItemsSold = v;
	}
}