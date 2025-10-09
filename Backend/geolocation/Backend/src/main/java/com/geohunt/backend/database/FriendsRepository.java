package com.geohunt.backend.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendsRepository extends JpaRepository<Friends, FriendKey> {
    List<Friends> findByPrimary(Account primary);

    List<Friends> findByTargetAndIsAcceptedTrue(Account target);

    boolean existsByPrimaryAndTarget(Account primary, Account target);
}
