package com.example.expense_tracker.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String messString) {
        super(messString);
    }
}
