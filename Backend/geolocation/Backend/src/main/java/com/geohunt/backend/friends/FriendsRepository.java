package com.geohunt.backend.friends;

import com.geohunt.backend.account.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, FriendKey> {
    List<Friends> findByPrimary(Account primary);

    List<Friends> findByTarget(Account target);

    List<Friends> findByTargetAndIsAcceptedTrue(Account target);

    List<Friends> findByTargetAndIsAcceptedFalse(Account target);

    List<Friends> findByPrimaryAndIsAcceptedTrue(Account target);

    List<Friends> findByPrimaryAndIsAcceptedFalse(Account target);

    boolean existsByPrimaryAndTarget(Account primary, Account target);

    Optional<Friends> findByPrimaryAndTarget(Account primary, Account target);

    @Transactional
    void deleteByPrimaryOrTarget(Account primary, Account target);

}


