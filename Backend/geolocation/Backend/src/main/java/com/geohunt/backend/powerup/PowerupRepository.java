package com.geohunt.backend.powerup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PowerupRepository extends JpaRepository<Powerup, Long> {
    Optional<Powerup> findById(long id);

    Optional<Powerup> findByName(String name);

    @Query(value = "SELECT * FROM powerup ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Powerup getRandomPowerup();
}
