package com.boardcamp.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalDTO {

    @NotNull(message = "Customer id must not be null")
    private Long customerId;

    @NotNull(message = "Game id must not be null")
    private Long gameId;

    @NotNull
    @Min(value = 1, message = "Days rented must be greater than 0")
    private int daysRented;
}
