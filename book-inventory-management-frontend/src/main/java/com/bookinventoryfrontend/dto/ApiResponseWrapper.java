package com.bookinventoryfrontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseWrapper<T> {
	private boolean success;
	private int statusCode;
	private String message;
	private T data;

	public ApiResponseWrapper() {
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean v) {
		this.success = v;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int v) {
		this.statusCode = v;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String v) {
		this.message = v;
	}

	public T getData() {
		return data;
	}

	public void setData(T v) {
		this.data = v;
	}
}
