package com.geohunt.backend.database;


import com.geohunt.backend.util.SHOP_ITEM_TYPE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByName(String name);
    List<Shop> findAllByItemType(SHOP_ITEM_TYPE itemType);
    Optional<Shop> findById(Long id);
}
