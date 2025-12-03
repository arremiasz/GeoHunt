package com.geohunt.backend.Services;

import com.geohunt.backend.database.Shop;
import com.geohunt.backend.database.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    public ResponseEntity<String> deleteItem(String name) {
        Optional<Shop> s = shopRepository.findByName(name);
        if (s.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
        }
        shopRepository.delete(s.get());
        //TODO Delete it from everyones inventory.
        return ResponseEntity.status(HttpStatus.OK).body("Shop Item deleted.");
    }

    public ResponseEntity<Shop> getItem(String name){
        Optional<Shop> item = shopRepository.findByName(name);

        return item.map(shop -> ResponseEntity.status(HttpStatus.OK).body(shop)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public boolean doesExist(String name) {
        Optional<Shop> item = shopRepository.findByName(name);

        return item.map(shop -> true).orElse(false);
    }

    public ResponseEntity<Shop> addItem(Shop shop) {
        if (doesExist(shop.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(shop);
        }
        shopRepository.save(shop);
        return ResponseEntity.status(HttpStatus.CREATED).body(shop);
    }
}
