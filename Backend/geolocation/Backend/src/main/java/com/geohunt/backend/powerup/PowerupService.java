package com.geohunt.backend.powerup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PowerupService {
    @Autowired
    private PowerupRepository repository;

    public ResponseEntity add(Powerup powerup) {
        if(repository.findByName(powerup.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        repository.save(powerup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity update() {
        if(repository.findByName(powerup.getName()).isPresent()){

        }
    }
}
