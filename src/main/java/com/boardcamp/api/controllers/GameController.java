package com.boardcamp.api.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.services.GameService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public String listAll() {
        return "To be implemented";
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid GameDTO body) {
        Optional<GameModel> game = gameService.save(body);
        if (game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Game already exists.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }

}
