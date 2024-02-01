package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boardcamp.api.models.RentalModel;

@Repository
public interface RentalRepository extends JpaRepository<RentalModel, Long> {
    @Query(value = "SELECT COUNT(*) FROM rentals r WHERE r.game_id = :gameId AND r.return_date IS NULL", nativeQuery = true)
    Long countByGameIdAndReturnDateNull(@Param("gameId") Long gameId);
}
