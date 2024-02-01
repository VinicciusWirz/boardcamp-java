package com.boardcamp.api.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.exceptions.CustomerNotFoundException;
import com.boardcamp.api.exceptions.GameNotFoundException;
import com.boardcamp.api.exceptions.RentalOutOfStockException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final GameRepository gameRepository;

    RentalService(RentalRepository rentalRepository, CustomerRepository customerRepository,
            GameRepository gameRepository) {
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.gameRepository = gameRepository;
    }

    public List<RentalModel> findAll() {
        return rentalRepository.findAll();
    }

    public RentalModel makeRental(RentalDTO dto) {
        Long gameId = dto.getGameId();
        Long customerId = dto.getCustomerId();

        CustomerModel customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        GameModel game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found"));

        long gamesRented = rentalRepository.countByGameIdAndReturnDateNull(gameId);
        if (gamesRented == game.getStockTotal()) {
            throw new RentalOutOfStockException("Game is out of stock");
        }

        RentalModel rental = new RentalModel(dto, customer, game);
        rental.setOriginalPrice(game.getPricePerDay() * dto.getDaysRented());
        rental.setDelayFee(Long.valueOf(0));
        rental.setRentDate(LocalDate.now());

        return rentalRepository.save(rental);
    }

    public String updateOne(Long id) {
        return "To be implemented";
    }
}
