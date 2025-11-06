package com.geohunt.backend.database;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, FriendKey> {
    List<Friends> findByPrimary(Account primary);

    List<Friends> findByTargetAndIsAcceptedTrue(Account target);

    List<Friends> findByPrimaryAndIsAcceptedTrue(Account target);

    boolean existsByPrimaryAndTarget(Account primary, Account target);

    Optional<Friends> findByPrimaryAndTarget(Account primary, Account target);

    @Transactional
    void deleteByPrimaryOrTarget(Account primary, Account target);

}


