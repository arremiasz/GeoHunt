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
        if(item.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(item.get());
    }
}
