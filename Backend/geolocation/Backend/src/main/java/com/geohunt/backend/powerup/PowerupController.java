package com.geohunt.backend.powerup;


import com.geohunt.backend.util.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/powerup")
public class PowerupController {
    //CONTROLLERS FOR DATABASE INTERACTION -> For dev *ONLY*

    @Autowired
    private PowerupService service;

    @PostMapping()
    public ResponseEntity addPowerup(@RequestBody Powerup powerup) {
        return service.add(powerup);
    }

    @DeleteMapping()
    public ResponseEntity removePowerup(@RequestParam long id) {
        return service.delete(id);
    }

    @GetMapping("/id")
    public ResponseEntity findById(@RequestParam long id) {
        return service.get(id);
    }

    @GetMapping("/name")
    public ResponseEntity findByName(@RequestParam String name) {
        return service.get(name);
    }

    //USE IN GAME:

    @PostMapping("/generate")
    public ResponseEntity challenge(@RequestParam long chalid, @RequestBody Location loc) {
        return service.generate(chalid, loc);
    }
}
