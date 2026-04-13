package com.bookinventory.user.common.response;

import java.time.LocalDateTime;

public class ApiResponse<T> {

	private boolean success;
	private int statusCode;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	public ApiResponse() {
		this.timestamp = LocalDateTime.now();
	}

	public ApiResponse(boolean success, int statusCode, String message, T data) {
		this.success = success;
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
		this.timestamp = LocalDateTime.now();
	}

	public static <T> ApiResponse<T> success(int code, String message, T data) {
		return new ApiResponse<>(true, code, message, data);
	}

	public static <T> ApiResponse<T> success(int code, String message) {
		return new ApiResponse<>(true, code, message, null);
	}

	public static <T> ApiResponse<T> failure(int code, String message) {
		return new ApiResponse<>(false, code, message, null);
	}

	// Getters & Setters
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean s) {
		this.success = s;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int c) {
		this.statusCode = c;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String m) {
		this.message = m;
	}

	public T getData() {
		return data;
	}

	public void setData(T d) {
		this.data = d;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime t) {
		this.timestamp = t;
	}
}