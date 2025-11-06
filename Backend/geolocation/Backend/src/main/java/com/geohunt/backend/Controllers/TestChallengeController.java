package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Placeholder Controller for creating challenge objects with the purpose of testing
 */
@RestController
public class TestChallengeController {

    @Autowired
    ChallengesRepository challengesRepository;

    @PostMapping("/test/challenge")
    public Challenges createChallenge(@RequestBody Challenges challenges){
        challengesRepository.save(challenges);
        return challenges;
    }

    @GetMapping("/test/challenge/{id}")
    public Challenges getChallenge(@PathVariable long id){
        if(challengesRepository.findById(id).isEmpty()){
            return null;
        }
        else{
            return challengesRepository.findById(id).get();
        }
    }
}
