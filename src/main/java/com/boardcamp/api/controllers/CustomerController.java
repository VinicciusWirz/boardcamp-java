package com.boardcamp.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @GetMapping("/{id}")
    public String findOne(@PathVariable Long id) {
        return "To be implemented";
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "To be implemented";
    }

}
