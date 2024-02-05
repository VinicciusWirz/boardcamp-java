package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDTO {

    @NotBlank(message = "Customer name cannot be empty")
    @NotNull(message = "Customer name cannot be null")
    private String name;

    @NotBlank(message = "Customer CPF cannot be empty")
    @NotNull(message = "Customer CPF cannot be null")
    @Pattern(regexp = "^\\d{11}$", message = "CPF must have length 11 and be only digits")
    private String cpf;

}
