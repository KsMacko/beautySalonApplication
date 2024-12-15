package com.example.salonclient.Model;

public class AppError {
    private int statusCode;
    private String message;
    public AppError() {}
    public AppError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    public int getStatusCode() {return statusCode;}
    public String getMessage() {return message;}
}
