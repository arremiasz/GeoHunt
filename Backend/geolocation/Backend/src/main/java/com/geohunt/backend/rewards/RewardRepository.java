package com.geohunt.backend.rewards;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    @Override
    Optional<Reward> findById(Long id);
}
