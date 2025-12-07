package com.geohunt.backend.powerup;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/powerup")
public class PowerupController {
    //CONTROLLERS FOR DATABASE INTERACTION -> For dev *ONLY*

    @Autowired
    private PowerupService service;

    @PostMapping("/add")
    public ResponseEntity addPowerup(@RequestBody Powerup powerup) {
        return service.add(powerup);
    }
}
