package com.geohunt.backend.database;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengesRepository extends JpaRepository<Challenges, Long> {
    List<Challenges> findByCreator_Id(Long accountId);
    Optional<Challenges> findById(long id);
    List<Challenges> getChallengesByCreator(Account account);
    Optional<Challenges> findByLatitudeAndLongitude(double latitude, double longitude);
    Optional<Challenges> findByStreetviewurl(String streetviewurl);
    @Transactional
    void deleteByCreator_Id(Long accountId);
}
