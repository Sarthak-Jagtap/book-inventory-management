package com.bookinventory.publisher.entity;

import com.bookinventory.state.entity.State;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "publisher")
public class Publisher {

	@Id
	@Column(name = "PublisherID")
	private int publisherId;

	@Column(name = "Name")
	private String name;

	@Column(name = "City")
	private String city;

	@ManyToOne
	@JoinColumn(name = "StateCode")
	private State state;

	public int getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(int publisherId) {
		this.publisherId = publisherId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

}
