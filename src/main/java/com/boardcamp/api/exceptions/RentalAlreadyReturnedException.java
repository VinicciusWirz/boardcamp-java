package com.boardcamp.api.exceptions;

public class RentalAlreadyReturnedException extends RuntimeException {
    public RentalAlreadyReturnedException(String message) {
        super(message);
    }
}
