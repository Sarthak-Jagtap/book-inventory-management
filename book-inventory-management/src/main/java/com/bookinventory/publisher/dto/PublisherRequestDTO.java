package com.bookinventory.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PublisherRequestDTO {

	@NotNull(message = "Publisher ID is required")
	private Integer publisherId;

	@NotBlank(message = "Publisher name is required")
	private String name;
	private String city;

	@NotNull(message = "State code is required")
	private String stateCode;

	public Integer getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Integer publisherId) {
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

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

}
