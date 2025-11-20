package com.geohunt.backend.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class geohuntController {

    @Autowired
    GeohuntService geohuntService;

    @Autowired
    ChallengesRepository challengesRepository;

    @GetMapping("/geohunt/getLocation")
    public ResponseEntity<Challenges> getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        try {
            Challenges c = geohuntService.getChallenge(lat, lng, radius);
            return ResponseEntity.ok().body(c);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/geohunt/getChallengeByID")
    public ResponseEntity<Challenges> getChallengeByID(@RequestParam long id) {
        try {
            Optional<Challenges> c = challengesRepository.findById(id);
            return ResponseEntity.ok().body(c.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @DeleteMapping("/geohunt/deleteByID")
    public ResponseEntity deleteChallengeByID(@RequestParam long id) {
        try {
            challengesRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/geohunt/createChallenge")
    public ResponseEntity createChallenge(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        try {
            List<Challenges> c = geohuntService.generateChallenges(lat, lng, radius, 1);
            return ResponseEntity.ok().body(c.get(c.size() - 1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/geohunt/randomChallenge")
    public ResponseEntity randChallenge(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        List<Challenges> l = geohuntService.fallbackGenerate(lat, lng, radius, 1);
        return ResponseEntity.ok().body(l.get(0));
    }

    @PostMapping("/geohunt/customChallenge")
    public ResponseEntity customChallenge(@RequestParam double lat, @RequestParam double lng, @RequestParam long uid, @RequestBody String url){
        return geohuntService.customChallenge(lat, lng, uid, url);
    }

    @GetMapping("/geohunt")
    public ResponseEntity getMyChallenges(@RequestParam long id) {
        return geohuntService.getUsersChallenges(id);
    }


    @DeleteMapping("/geohunt/mySubmissions")
    public ResponseEntity<String> deleteMySubmissions(@RequestParam long userId, @RequestParam long chalId){return geohuntService.deleteUsersChallenges(userId, chalId);}

    @PutMapping("/geohunt/mySubmission/updateChallenge")
    public ResponseEntity updateMyChallenge(@RequestParam long userId, @RequestParam long chalId, @RequestParam double lat, @RequestParam double lng, @RequestBody String image){
        return geohuntService.updateChallenge(userId, chalId, image, lng, lat);
    }
}
