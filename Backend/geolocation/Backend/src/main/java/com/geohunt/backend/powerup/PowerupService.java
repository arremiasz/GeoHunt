package com.geohunt.backend.powerup;

import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import com.geohunt.backend.util.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Optional;
import java.util.random.RandomGenerator;

@Service
public class PowerupService {

    @Autowired
    private PowerupRepository repository;

    @Autowired
    private GeohuntService geohuntService;
    @Autowired
    private ChallengesRepository challengesRepository;

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
        Optional<Challenges> c = challengesRepository.findById(chalid);

        if(c.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Challenges chal = c.get();

        Random random = new Random();

        int min = 30;
        int max = 70;

        int randomNumber = random.nextInt(max - min + 1) + min;

        double t = (randomNumber / 100.0);

        double newLat = loc.getLatitude() + t * (chal.getLatitude() - loc.getLatitude());
        double newLon = loc.getLongitude() + t * (chal.getLongitude() - loc.getLongitude());

        double dx = chal.getLongitude() - loc.getLongitude();
        double dy = chal.getLatitude() - loc.getLatitude();

        double len = Math.sqrt(dy * dy + dx * dx);

        double px = -dy / len;
        double py = dx / len;

        randomNumber = random.nextInt(max - min + 1) + min;

        double latBaseRadians = Math.toRadians(newLat);

        double MetersToLat = randomNumber / 111000;
        double MetersToLon = randomNumber / (111000 * Math.cos(latBaseRadians));

        double sign = random.nextBoolean() ? 1 : -1;

        double lat_powerup = newLat + sign * py * MetersToLat;
        double lon_powerup = newLon + sign * px * MetersToLon;

        RandomGenerationDTO rgd = new RandomGenerationDTO();
        rgd.setLon(lon_powerup);
        rgd.setLat(lat_powerup);
        rgd.setPowerup(pUp);

        return ResponseEntity.status(HttpStatus.CREATED).body(rgd);
    }
}
