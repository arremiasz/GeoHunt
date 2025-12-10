package com.geohunt.backend.rewards;

import com.geohunt.backend.Shop.Shop;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    @Transactional
    void deleteByShopItem(Shop shop);
}
