package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<Challenges> getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        try{
            Challenges c = geohuntService.getChallenge(lat, lng, radius);
            return ResponseEntity.ok().body(c);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/geohunt/getChallengeByID")
    public ResponseEntity<Challenges> getChallengeByID(@RequestParam long id) {
        try{
            Challenges c = challengesRepository.findById(id);
            return ResponseEntity.ok().body(c);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @DeleteMapping("/geohunt/deleteByID")
    public ResponseEntity deleteChallengeByID(@RequestParam long id) {
        try{
            challengesRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
