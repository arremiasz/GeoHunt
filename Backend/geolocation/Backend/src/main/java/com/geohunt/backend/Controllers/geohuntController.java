package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class geohuntController {

    @Autowired
    GeohuntService geohuntService;

    @Autowired
    ChallengesRepository challengesRepository;

    @GetMapping("/geohunt/getLocation")
    public ResponseEntity<String> getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        String c = geohuntService.getChallenge(lat, lng, radius);
        return ResponseEntity.ok().body(c);
    }
}
