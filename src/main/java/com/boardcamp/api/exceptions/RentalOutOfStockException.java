package com.boardcamp.api.exceptions;

public class RentalOutOfStockException extends RuntimeException {
    public RentalOutOfStockException(String message) {
        super(message);
    }
}
