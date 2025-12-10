package com.geohunt.backend.database;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author evan juslon
 */
@Repository
public interface SubmissionsRepository extends JpaRepository<Submissions, Long> {
    List<Submissions> findBySubmitter_Id(Long accountId);
    @Transactional
    void deleteBySubmitter_Id(Long id);
    @Transactional
    void deleteByChallenge_Id(Long challengeId);
}
