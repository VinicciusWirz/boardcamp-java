package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
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

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

import jakarta.validation.ValidationException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RentalIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        rentalRepository.deleteAll();
        customerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void givenGetAllRequest_whenThereAreRentals_thenReturnListOfRentals() {
        int amountOfRentals = 5;
        GameDTO gameDTO = new GameDTO("name", "image", amountOfRentals, 1500L);
        GameModel gameModel = new GameModel(gameDTO);
        GameModel gameSaved = gameRepository.save(gameModel);

        CustomerDTO customerDTO = new CustomerDTO("name", "12345678901");
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customerSaved = customerRepository.save(customerModel);

        for (int amount = amountOfRentals; amount > 0; amount--) {
            RentalDTO rentalDTO = new RentalDTO(customerSaved.getId(), gameSaved.getId(), 3);
            RentalModel rentalModel = new RentalModel(rentalDTO, customerSaved, gameSaved);
            rentalModel.setDelayFee(0L);
            rentalModel.setOriginalPrice(gameSaved.getPricePerDay() * 3);
            rentalModel.setRentDate(LocalDate.now());
            rentalRepository.save(rentalModel);
        }

        ParameterizedTypeReference<List<RentalModel>> responseType = new ParameterizedTypeReference<List<RentalModel>>() {
        };

        ResponseEntity<List<RentalModel>> response = testRestTemplate.exchange(
                "/rentals",
                HttpMethod.GET,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(amountOfRentals, response.getBody().size());
        assertEquals(amountOfRentals, rentalRepository.count());
    }

    @Test
    void givenValidDTO_whenMakingNewRental_thenReturnNewRental() {
        CustomerDTO customerDTO = new CustomerDTO("name", "12345678901");
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customerSaved = customerRepository.save(customerModel);

        int pricePerDay = 1500;
        GameDTO gameDTO = new GameDTO("name", "image", 3, Long.valueOf(pricePerDay));
        GameModel gameModel = new GameModel(gameDTO);
        GameModel gameSaved = gameRepository.save(gameModel);

        int daysRented = 3;
        RentalDTO rentalDTO = new RentalDTO(customerSaved.getId(), gameSaved.getId(), daysRented);
        int expectedOriginalPrice = pricePerDay * daysRented;

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<RentalModel> response = testRestTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                RentalModel.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(gameSaved, response.getBody().getGame());
        assertEquals(customerSaved, response.getBody().getCustomer());
        assertEquals(daysRented, response.getBody().getDaysRented());
        assertEquals(expectedOriginalPrice, response.getBody().getOriginalPrice());
        assertEquals(0, response.getBody().getDelayFee());
        assertEquals(LocalDate.now(), response.getBody().getRentDate());
        assertNull(response.getBody().getReturnDate());
        assertEquals(1, rentalRepository.count());
    }

    @Test
    void givenInvalidDTO_whenMakingNewRental_thenThrowsError() {
        RentalDTO rentalDTO = new RentalDTO(null, null, 0);
        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<ValidationException> response = testRestTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                ValidationException.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed for object='rentalDTO'. Error count: 3", response.getBody().getMessage());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenInvalidCustomerID_whenMakingNewRental_thenThrowsError() {
        RentalDTO rentalDTO = new RentalDTO(1L, 1L, 3);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                String.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found", response.getBody());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenInvalidGameID_whenMakingNewRental_thenThrowsError() {
        CustomerDTO customerDTO = new CustomerDTO("name", "12345678901");
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customerSaved = customerRepository.save(customerModel);

        RentalDTO rentalDTO = new RentalDTO(customerSaved.getId(), 1L, 3);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                String.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Game not found", response.getBody());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenOutOfStockGameID_whenMakingNewRental_thenThrowsError() {
        CustomerDTO customerDTO = new CustomerDTO("name", "12345678901");
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customerSaved = customerRepository.save(customerModel);

        GameDTO gameDTO = new GameDTO("name", "image", 1, 1500L);
        GameModel gameModel = new GameModel(gameDTO);
        GameModel gameSaved = gameRepository.save(gameModel);

        RentalDTO rentalDTO = new RentalDTO(customerSaved.getId(), gameSaved.getId(), 3);
        RentalModel rentalModel = new RentalModel(rentalDTO, customerSaved, gameSaved);
        rentalModel.setDelayFee(0L);
        rentalModel.setOriginalPrice(gameSaved.getPricePerDay() * 3);
        rentalModel.setRentDate(LocalDate.now());
        rentalRepository.save(rentalModel);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                String.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Game is out of stock", response.getBody());
        assertEquals(1, rentalRepository.count());
    }

    @Test
    void givenValidOpenRental_whenReturningRental_thenReturnsUpdatedRental() {
        CustomerDTO customerDTO = new CustomerDTO("name", "12345678901");
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customerSaved = customerRepository.save(customerModel);

        int pricePerDay = 1500;
        GameDTO gameDTO = new GameDTO("name", "image", 3, Long.valueOf(pricePerDay));
        GameModel gameModel = new GameModel(gameDTO);
        GameModel gameSaved = gameRepository.save(gameModel);

        LocalDate today = LocalDate.now();
        int daysRented = 3;
        RentalDTO rentalDTO = new RentalDTO(customerSaved.getId(), gameSaved.getId(), daysRented);
        RentalModel rental = new RentalModel(rentalDTO, customerSaved, gameSaved);
        rental.setOriginalPrice(gameSaved.getPricePerDay() * rentalDTO.getDaysRented());
        rental.setDelayFee(0L);
        rental.setRentDate(today);
        RentalModel savedRental = rentalRepository.save(rental);

        int expectedOriginalPrice = pricePerDay * daysRented;

        ResponseEntity<RentalModel> response = testRestTemplate.exchange(
                "/rentals/{id}/return",
                HttpMethod.PUT,
                null,
                RentalModel.class,
                savedRental.getId());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gameSaved, response.getBody().getGame());
        assertEquals(customerSaved, response.getBody().getCustomer());
        assertEquals(expectedOriginalPrice, response.getBody().getOriginalPrice());
        assertEquals(0, response.getBody().getDelayFee());
        assertEquals(today, response.getBody().getRentDate());
        assertEquals(today, response.getBody().getReturnDate());
        assertEquals(1, rentalRepository.count());
    }

    @Test
    void givenValidOpenDelayedRental_whenReturningRental_thenReturnsUpdatedRentalWithFee() {
        CustomerDTO customerDTO = new CustomerDTO("name", "12345678901");
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customerSaved = customerRepository.save(customerModel);

        int pricePerDay = 1500;
        GameDTO gameDTO = new GameDTO("name", "image", 3, Long.valueOf(pricePerDay));
        GameModel gameModel = new GameModel(gameDTO);
        GameModel gameSaved = gameRepository.save(gameModel);

        LocalDate today = LocalDate.now();
        int daysRented = 3;
        int daysLate = 2;
        LocalDate rentDate = today.minusDays(daysRented + daysLate);

        RentalDTO rentalDTO = new RentalDTO(customerSaved.getId(), gameSaved.getId(), daysRented);
        RentalModel rental = new RentalModel(rentalDTO, customerSaved, gameSaved);
        rental.setOriginalPrice(gameSaved.getPricePerDay() * rentalDTO.getDaysRented());
        rental.setDelayFee(0L);
        rental.setRentDate(rentDate);
        RentalModel savedRental = rentalRepository.save(rental);

        int expectedOriginalPrice = pricePerDay * daysRented;

        ResponseEntity<RentalModel> response = testRestTemplate.exchange(
                "/rentals/{id}/return",
                HttpMethod.PUT,
                null,
                RentalModel.class,
                savedRental.getId());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gameSaved, response.getBody().getGame());
        assertEquals(customerSaved, response.getBody().getCustomer());
        assertEquals(expectedOriginalPrice, response.getBody().getOriginalPrice());
        assertEquals(daysLate * pricePerDay, response.getBody().getDelayFee());
        assertEquals(rentDate, response.getBody().getRentDate());
        assertEquals(today, response.getBody().getReturnDate());
        assertEquals(1, rentalRepository.count());
    }

    @Test
    void givenInvalidRentalID_whenReturningRental_thenThrowsError() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/rentals/{id}/return",
                HttpMethod.PUT,
                null,
                String.class,
                1L);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Rental was not found", response.getBody());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenAlreadyReturnedRental_whenReturningRental_thenThrowsError() {
        CustomerDTO customerDTO = new CustomerDTO("name", "12345678901");
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customerSaved = customerRepository.save(customerModel);

        int pricePerDay = 1500;
        GameDTO gameDTO = new GameDTO("name", "image", 3, Long.valueOf(pricePerDay));
        GameModel gameModel = new GameModel(gameDTO);
        GameModel gameSaved = gameRepository.save(gameModel);

        LocalDate today = LocalDate.now();
        int daysRented = 3;

        RentalDTO rentalDTO = new RentalDTO(customerSaved.getId(), gameSaved.getId(), daysRented);
        RentalModel rental = new RentalModel(rentalDTO, customerSaved, gameSaved);
        rental.setOriginalPrice(gameSaved.getPricePerDay() * rentalDTO.getDaysRented());
        rental.setDelayFee(0L);
        rental.setRentDate(today);
        rental.setReturnDate(today);
        RentalModel savedRental = rentalRepository.save(rental);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/rentals/{id}/return",
                HttpMethod.PUT,
                null,
                String.class,
                savedRental.getId());

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("This rental was already returned", response.getBody());
        assertEquals(1, rentalRepository.count());
    }
}
