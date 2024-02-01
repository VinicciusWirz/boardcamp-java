package com.boardcamp.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    @GetMapping
    public String findAll() {
        return "To be implemented";
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "To be implemented";
    }

    @PutMapping("/{id}/return")
    public String putMethodName(@PathVariable Long id) {
        return "To be implemented";
    }

}
