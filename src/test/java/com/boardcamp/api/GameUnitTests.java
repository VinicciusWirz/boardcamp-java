package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.exceptions.GameConflictException;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.services.GameService;

@SpringBootTest
@ActiveProfiles("test")
class GameUnitTests {
    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Test
    void givenValidGameName_whenRegisteringAGame_thenRegisterNewGame() {
        GameDTO dto = new GameDTO("name", "image", 1, 1L);
        GameModel newGame = new GameModel(dto);

        doReturn(false).when(gameRepository).existsByName(any());
        doReturn(newGame).when(gameRepository).save(any());

        GameModel result = gameService.save(dto);

        assertNotNull(result);
        assertEquals(newGame, result);
        verify(gameRepository, times(1)).existsByName(any());
        verify(gameRepository, times(1)).save(any());
    }

    @Test
    void givenAlreadyRegisteredName_whenRegisteringAGame_thenThrowsError() {
        GameDTO dto = new GameDTO("name", "image", 1, 1L);

        doReturn(true).when(gameRepository).existsByName(any());

        GameConflictException exception = assertThrows(GameConflictException.class, () -> gameService.save(dto));

        assertNotNull(exception);
        assertEquals("Game already exists.", exception.getMessage());
        verify(gameRepository, times(1)).existsByName(any());
        verify(gameRepository, times(0)).save(any());
    }
}
