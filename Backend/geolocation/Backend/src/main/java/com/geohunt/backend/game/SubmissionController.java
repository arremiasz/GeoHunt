package com.geohunt.backend.game;

import com.geohunt.backend.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubmissionController {

    @Autowired
    SubmissionsService submissionsService;

    @Autowired
    ChallengesRepository challengesRepository;

    @Autowired
    AccountService accountService;

    /**
     *
     * @RequestBody Submissions submission
     * @RequestParam long uid
     * @RequestParam long cid
     * @return Submissions as JSON
     */

    // Post Submission
    @PostMapping(value = "/geohunt/submission", consumes = "application/json")
    public ResponseEntity<Double> saveSubmission(@RequestBody Submissions submission, @RequestParam long uid, @RequestParam long cid){
        Submissions savedSubmission;
        double distance;
        try{
            savedSubmission = submissionsService.saveSubmission(submission, uid, cid);
            distance = savedSubmission.distanceFromChallenge();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body(-1.0);
        }
        return ResponseEntity.status(200).body(distance);
    }

    // Get Submission
    @GetMapping("/geohunt/submission/{id}")
    public ResponseEntity<Submissions> getSubmission(@PathVariable long id){
        try {
            Submissions submission = submissionsService.getSubmissionById(id);
            return ResponseEntity.status(200).body(submission);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(null);
        }
    }

    // Put / Update Submission
    @PutMapping("/geohunt/submission/{id}")
    public ResponseEntity<Submissions> updateSubmission(@RequestBody Submissions updatedValues, @PathVariable long id){

        try {
            Submissions submission = submissionsService.updateSubmission(updatedValues,id);
            return ResponseEntity.status(200).body(submission);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body(null);
        }
    }

    // Delete Submission
    @DeleteMapping("/geohunt/submission/{id}")
    public ResponseEntity<String> deleteSubmission(@PathVariable long id){
        // Remove Submission
        boolean removed = submissionsService.deleteSubmissionById(id);

        if(removed){
            return ResponseEntity.status(200).body("submission removed.");
        }
        else {
            return ResponseEntity.status(404).body("cannot find submission to delete.");
        }
    }

    // List Submissions by user
    @GetMapping("/account/{uid}/submissions")
    public ResponseEntity<List<Submissions>> listSubmissionsWithUser(@PathVariable long uid){
        try{
            List<Submissions> submissionsList = submissionsService.getSubmissionListByAccount(uid);
            return ResponseEntity.status(200).body(submissionsList);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(null);
        }
    }

    // List Submissions by challenge
    @GetMapping("/geohunt/challenge/{cid}/submissions")
    public ResponseEntity<List<Submissions>> listSubmissionsWithChallenge(@PathVariable long cid){
        try{
            List<Submissions> submissionsList = submissionsService.getSubmissionListByChallenge(cid);
            return ResponseEntity.status(200).body(submissionsList);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(null);
        }
    }
}
