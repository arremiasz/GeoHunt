package com.geohunt.backend.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengesRepository extends JpaRepository<Challenges, Long> {
    List<Challenges> findByCreator_Id(Long accountId);


}
