package com.bookinventoryfrontend.wrapper;
public class ApiResponse<T> {

    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}