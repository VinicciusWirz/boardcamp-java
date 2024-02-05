package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;

import jakarta.validation.ValidationException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GameIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        gameRepository.deleteAll();
    }

    @Test
    void givenValidGameDTO_whenRegisteringAGame_thenRegisterNewGame() {
        GameDTO dto = new GameDTO(
                "name",
                "image",
                1,
                1500L);

        HttpEntity<GameDTO> body = new HttpEntity<GameDTO>(dto);

        ResponseEntity<GameModel> response = testRestTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body,
                GameModel.class);

        GameModel expectedBody = new GameModel(dto, response.getBody().getId());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedBody, response.getBody());
        assertEquals(1, gameRepository.count());
    }

    @Test
    void givenInvalidGameDTO_whenRegisteringAGame_thenThrowsError() {
        GameDTO dto = new GameDTO(
                "",
                "image",
                0,
                0L);

        HttpEntity<GameDTO> body = new HttpEntity<GameDTO>(dto);

        ResponseEntity<ValidationException> response = testRestTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body,
                ValidationException.class);

        assertNotNull(response.getBody());
        assertEquals("Validation failed for object='gameDTO'. Error count: 3", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gameRepository.count());
        System.out.println(response.getBody());
    }

    @Test
    void givenAlreadyRegisteredName_whenRegisteringAGame_thenThrowsError() {
        GameDTO dto = new GameDTO(
                "name",
                "image",
                1,
                1500L);

        GameModel existingGame = new GameModel(dto);
        gameRepository.save(existingGame);

        HttpEntity<GameDTO> body = new HttpEntity<GameDTO>(dto);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body,
                String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Game already exists.", response.getBody());
        assertEquals(1, gameRepository.count());
    }

    @Test
    void givenGetAllRequest_whenThereAreGames_thenReturnListOfGames() {
        for (int amountOfGames = 5; amountOfGames > 0; amountOfGames--) {
            String gameName = ("name" + amountOfGames);
            GameDTO dto = new GameDTO(
                    gameName,
                    "image",
                    1,
                    1500L);

            GameModel game = new GameModel(dto);
            gameRepository.save(game);
        }

        ParameterizedTypeReference<List<GameModel>> responseType = new ParameterizedTypeReference<List<GameModel>>() {
        };

        ResponseEntity<List<GameModel>> response = testRestTemplate.exchange(
                "/games",
                HttpMethod.GET,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().size());
        assertEquals(5, gameRepository.count());
    }

}
