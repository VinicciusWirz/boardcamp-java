package com.boardcamp.api.exceptions;

public class CustomerCPFConflict extends RuntimeException {
    public CustomerCPFConflict(String message) {
        super(message);
    }
}
