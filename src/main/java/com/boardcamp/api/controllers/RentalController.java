package com.boardcamp.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.services.RentalService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public ResponseEntity<List<RentalModel>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(rentalService.findAll());
    }

    @PostMapping
    public ResponseEntity<RentalModel> create(@RequestBody @Valid RentalDTO body) {
        RentalModel rental = rentalService.makeRental(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<RentalModel> putMethodName(@PathVariable Long id) {
        RentalModel rental = rentalService.finishRental(id);
        return ResponseEntity.status(HttpStatus.OK).body(rental);
    }

}
