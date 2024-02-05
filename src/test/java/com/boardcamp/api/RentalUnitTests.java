package com.boardcamp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.RentalAlreadyReturnedException;
import com.boardcamp.api.exceptions.RentalNotFoundException;
import com.boardcamp.api.exceptions.RentalOutOfStockException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;
import com.boardcamp.api.services.RentalService;

@SpringBootTest
@ActiveProfiles("test")
class RentalUnitTests {
    @InjectMocks
    private RentalService rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GameRepository gameRepository;

    @Test
    void givenValidCustomerAndGame_whenMakingNewRental_thenReturnsNewRental() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        CustomerDTO customerDTO = new CustomerDTO("customerName", "12345678901");

        CustomerModel customer = new CustomerModel(customerDTO);
        customer.setId(1L);

        GameDTO gameDTO = new GameDTO(
                "gameName",
                "image",
                1,
                1500L);

        GameModel game = new GameModel(gameDTO);
        game.setId(1L);

        RentalModel rental = new RentalModel(dto, customer, game);

        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(0L).when(rentalRepository).countByGameIdAndReturnDateNull(any());
        doReturn(rental).when(rentalRepository).save(any());

        RentalModel result = rentalService.makeRental(dto);

        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(game, result.getGame());
        verify(customerRepository, times(1)).findById(any());
        verify(gameRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).countByGameIdAndReturnDateNull(any());
        verify(rentalRepository, times(1)).save(any());
    }

    @Test
    void givenInvalidCustomerID_whenMakingNewRental_thenThrowsError() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);
        doReturn(Optional.empty()).when(customerRepository).findById(any());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> rentalService.makeRental(dto));

        assertNotNull(exception);
        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(any());
        verify(gameRepository, times(0)).findById(any());
        verify(rentalRepository, times(0)).countByGameIdAndReturnDateNull(any());
        verify(rentalRepository, times(0)).save(any());
    }

    @Test
    void givenInvalidGameID_whenMakingNewRental_thenThrowsError() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        CustomerDTO customerDTO = new CustomerDTO("customerName", "12345678901");

        CustomerModel customer = new CustomerModel(customerDTO);
        customer.setId(1L);

        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(Optional.empty()).when(gameRepository).findById(any());

        GameNotFoundException exception = assertThrows(GameNotFoundException.class,
                () -> rentalService.makeRental(dto));

        assertNotNull(exception);
        assertEquals("Game not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(any());
        verify(gameRepository, times(1)).findById(any());
        verify(rentalRepository, times(0)).countByGameIdAndReturnDateNull(any());
        verify(rentalRepository, times(0)).save(any());
    }

    @Test
    void givenOutOfStockGameID_whenMakingNewRental_thenThrowsError() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        CustomerDTO customerDTO = new CustomerDTO("customerName", "12345678901");

        CustomerModel customer = new CustomerModel(customerDTO);
        customer.setId(1L);

        GameDTO gameDTO = new GameDTO(
                "gameName",
                "image",
                3,
                1500L);

        GameModel game = new GameModel(gameDTO);
        game.setId(1L);

        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(3L).when(rentalRepository).countByGameIdAndReturnDateNull(any());

        RentalOutOfStockException exception = assertThrows(RentalOutOfStockException.class,
                () -> rentalService.makeRental(dto));

        assertNotNull(exception);
        assertEquals("Game is out of stock", exception.getMessage());
        verify(customerRepository, times(1)).findById(any());
        verify(gameRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).countByGameIdAndReturnDateNull(any());
        verify(rentalRepository, times(0)).save(any());
    }

    @Test
    void givenValidRentalID_whenFinishingRental_thenReturnsFinishedRental() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        CustomerDTO customerDTO = new CustomerDTO("customerName", "12345678901");

        CustomerModel customer = new CustomerModel(customerDTO);
        customer.setId(1L);

        GameDTO gameDTO = new GameDTO(
                "gameName",
                "image",
                1,
                1500L);

        GameModel game = new GameModel(gameDTO);
        game.setId(1L);

        RentalModel rental = new RentalModel(dto, customer, game);
        rental.setRentDate(LocalDate.now());

        doReturn(Optional.of(rental)).when(rentalRepository).findById(any());
        doReturn(rental).when(rentalRepository).save(any());

        RentalModel result = rentalService.finishRental(1L);

        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(game, result.getGame());
        assertEquals(0, result.getDelayFee());
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).save(any());
    }

    @Test
    void givenLateRental_whenFinishingRental_thenReturnsFinishedRentalWithDelayFee() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        CustomerDTO customerDTO = new CustomerDTO("customerName", "12345678901");

        CustomerModel customer = new CustomerModel(customerDTO);
        customer.setId(1L);

        GameDTO gameDTO = new GameDTO(
                "gameName",
                "image",
                1,
                1500L);

        GameModel game = new GameModel(gameDTO);
        game.setId(1L);

        RentalModel rental = new RentalModel(dto, customer, game);
        rental.setRentDate(LocalDate.now().minusDays(5));

        doReturn(Optional.of(rental)).when(rentalRepository).findById(any());
        doReturn(rental).when(rentalRepository).save(any());

        RentalModel result = rentalService.finishRental(1L);

        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(game, result.getGame());
        assertEquals(((5 - 3) * game.getPricePerDay()), result.getDelayFee());
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).save(any());
    }

    @Test
    void givenInvalidRentalID_whenFinishingRental_thenThrowsError() {
        doReturn(Optional.empty()).when(rentalRepository).findById(any());

        RentalNotFoundException exception = assertThrows(RentalNotFoundException.class,
                () -> rentalService.finishRental(1L));

        assertNotNull(exception);
        assertEquals("Rental was not found", exception.getMessage());
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(0)).save(any());
    }

    @Test
    void givenAlreadyReturnedRental_whenFinishingRental_thenThrowsError() {
        RentalDTO dto = new RentalDTO(1L, 1L, 3);

        CustomerDTO customerDTO = new CustomerDTO("customerName", "12345678901");

        CustomerModel customer = new CustomerModel(customerDTO);
        customer.setId(1L);

        GameDTO gameDTO = new GameDTO(
                "gameName",
                "image",
                1,
                1500L);

        GameModel game = new GameModel(gameDTO);
        game.setId(1L);

        RentalModel rental = new RentalModel(dto, customer, game);
        rental.setRentDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now());

        doReturn(Optional.of(rental)).when(rentalRepository).findById(any());

        RentalAlreadyReturnedException exception = assertThrows(RentalAlreadyReturnedException.class,
                () -> rentalService.finishRental(1L));

        assertNotNull(exception);
        assertEquals("This rental was already returned", exception.getMessage());
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(0)).save(any());
    }

}
