package com.geohunt.backend.Shop;

import com.geohunt.backend.database.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Optional<Transactions> findById(Long id);
    List<Transactions> findByUser(Account user);
    @Transactional
    void deleteAllByUser(Account user);
}
