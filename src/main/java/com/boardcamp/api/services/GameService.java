package com.boardcamp.api.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;

@Service
public class GameService {
    private final GameRepository gameRepository;

    GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public String findAll() {
        return "To be implemented";
    }

    public Optional<GameModel> save(GameDTO dto) {
        if (gameRepository.existsByName(dto.getName())) {
            // TODO: better exception handling
            return Optional.empty();
        }
        GameModel game = new GameModel(dto);

        return Optional.of(gameRepository.save(game));
    }

}
