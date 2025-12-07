package com.geohunt.backend.powerup;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Random;

@Repository
public interface PowerupRepository extends JpaRepository<Powerup, Long> {
    Optional<Powerup> findById(long id);

    Optional<Powerup> findByName(String name);

    @Query(value = "SELECT COUNT(*) FROM powerup", nativeQuery = true)
    int countPowerups();

    default Powerup getRandomPowerupPortable() {
        int count = countPowerups();
        int index = new Random().nextInt(count);
        return findAll(PageRequest.of(index, 1)).getContent().get(0);
    }
}
