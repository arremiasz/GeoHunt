package com.geohunt.backend.powerup;

import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.util.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PowerupService {

    @Autowired
    private PowerupRepository repository;

    @Autowired
    private GeohuntService geohuntService;

    public ResponseEntity add(Powerup powerup) {
        if(repository.findByName(powerup.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        repository.save(powerup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity delete(long id) {
        if(repository.findById(id).isPresent()){
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity get(long id) {
        Optional<Powerup> powerup = repository.findById(id);
        if(powerup.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(powerup.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity get(String name) {
        Optional<Powerup> powerup = repository.findByName(name);
        if(powerup.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(powerup.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity generate(long chalid, Location loc) {
        Powerup pUp = repository.getRandomPowerup();
        return ResponseEntity.status(HttpStatus.OK).body(pUp);
    }
}
