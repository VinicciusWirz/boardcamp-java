package com.boardcamp.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/games")
public class GameController {

    @GetMapping
    public String listAll() {
        return "To be implemented";
    }

    @PostMapping
    public String create(@RequestBody String body) {
        return "To be implemented";
    }

}
