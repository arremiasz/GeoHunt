package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.*;
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
    @PostMapping("/geohunt/submission")
    public ResponseEntity<Submissions> saveSubmission(@RequestBody Submissions submission, @RequestParam long uid, @RequestParam long cid){
        try{
            return ResponseEntity.status(200).body( submissionsService.saveSubmission(submission,uid,cid) );
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body(null);
        }
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

    // Get User of Submission

    // Get Challenge of Submission

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

//        // Get Submission with Id
//        if(submissionsRepository.findById(id).isEmpty()){
//            return ResponseEntity.status(404).body(null); // No submission exists.
//        }
//        Submissions submissionToUpdate = submissionsRepository.findById(id).get();
//
//        // Update Submission values
//        submissionToUpdate.updateValues(updatedValues);
//
//        // Save Submission
//        submissionsRepository.save(submissionToUpdate);
//
//        // Return updated Submission
//        return ResponseEntity.status(200).body(submissionToUpdate);
    }

    // Delete Submission
    @DeleteMapping("/geohunt/submission/{id}")
    public ResponseEntity<String> deleteSubmission(@PathVariable long id){
        // Remove Submission
        submissionsService.deleteSubmissionById(id);

        // Need to check what should be returned
        return ResponseEntity.status(200).body("submission removed.");

        // TODO: Check if the submission needs to be unlinked from the challenge and user objects.
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
        // TODO: Test with challenges code once merging.
        try{
            List<Submissions> submissionsList = submissionsService.getSubmissionListByChallenge(cid);
            return ResponseEntity.status(200).body(submissionsList);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(null);
        }
    }
}
