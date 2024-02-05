package com.boardcamp.api.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameDTO {

    @NotBlank(message = "Game must have a name")
    @NotNull(message = "Game name must not be null")
    private String name;

    private String image;

    @NotNull
    @Min(value = 1, message = "Stock total must be greater than 0")
    private int stockTotal;

    @NotNull
    @Min(value = 1, message = "Price per day must be greater than 0")
    private Long pricePerDay;
}
