package com.boardcamp.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ GameConflictException.class })
    public ResponseEntity<Object> handlerGameConflict(GameConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ GameNotFoundException.class })
    public ResponseEntity<Object> handlerGameNotFound(GameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({ CustomerCPFConflict.class })
    public ResponseEntity<Object> handlerCustomerCPFConflict(CustomerCPFConflict exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ CustomerNotFoundException.class })
    public ResponseEntity<Object> handlerCustomerNotFoundException(CustomerNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({ RentalOutOfStockException.class })
    public ResponseEntity<Object> handlerRentalOutOfStockException(RentalOutOfStockException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }

    @ExceptionHandler({ RentalNotFoundException.class })
    public ResponseEntity<Object> handlerRentalNotFoundException(RentalNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({ RentalAlreadyReturnedException.class })
    public ResponseEntity<Object> handlerRentalAlreadyReturnedException(RentalAlreadyReturnedException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exception.getMessage());
    }
}
