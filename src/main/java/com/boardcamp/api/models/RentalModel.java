package com.boardcamp.api.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rentals")
public class RentalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private GameModel game;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private CustomerModel customer;

    @Column(nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate rentDate;

    @Column(nullable = false)
    private int daysRented;

    @Column(nullable = true)
    private LocalDate returnDate;

    @Column(nullable = false)
    private Long originalPrice;

    @Column(nullable = false)
    private Long delayFee;

}
