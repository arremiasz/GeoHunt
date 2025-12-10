package com.geohunt.backend.Shop;

import com.geohunt.backend.database.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInventoryRepository extends JpaRepository<UserInventory, Long> {
    List<UserInventory> findAllByUser(Account user);

    List<UserInventory> findAllByUserId(Long userId);

    Optional<UserInventory> findByUserIdAndShopItemId(Long userId, Long shopItemId);

    Optional<UserInventory> findByShopItem(Shop shopItem);

    @Transactional
    void deleteAllByShopItem(Shop shop);

    @Transactional
    void deleteAllByUser(Account user);

    @Transactional
    void deleteByUserIdAndShopItem(Long userId, Shop shop);

    @Transactional
    void deleteById(Long id);

    Optional<UserInventory> findByUserIdAndShopItemId(long userId, long shopItemId);
}
