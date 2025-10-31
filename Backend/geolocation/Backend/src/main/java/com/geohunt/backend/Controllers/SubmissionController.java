package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubmissionController {

    @Autowired
    SubmissionsRepository submissionsRepository;

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
    public ResponseEntity<Submissions> receiveSubmission(@RequestBody Submissions submission, @RequestParam long uid, @RequestParam long cid){

        // Set Challenge and Account that the submission is tied to.
        submission.setSubmitter(accountService.getAccountById(uid));
        submission.setChallenge(null); // TODO: Implement challenges when merging with Location Generation

        if(!submission.validate()){
            return ResponseEntity.status(400).body(null);
        }

        // Add Submission to Database
        submissionsRepository.save(submission);

        // Return a copy of the submission as JSON
        return ResponseEntity.status(200).body(submission);
    }

    // Get Submission
    @GetMapping("/geohunt/submission/{id}")
    public ResponseEntity<Submissions> getSubmission(@PathVariable long id){
        if(submissionsRepository.findById(id).isEmpty()){
            // No submission with given id exists.
            return ResponseEntity.status(404).body(null);
        }
        // Return submission as JSON
        Submissions submission = submissionsRepository.findById(id).get();
        return ResponseEntity.status(200).body(submission);
    }

    // Put / Update Submission
    @PutMapping("/geohunt/submission/{id}")
    public ResponseEntity<Submissions> updateSubmission(@RequestBody Submissions updatedValues, @PathVariable long id){
        // Get Submission with Id
        if(submissionsRepository.findById(id).isEmpty()){
            return ResponseEntity.status(404).body(null); // No submission exists.
        }
        Submissions submissionToUpdate = submissionsRepository.findById(id).get();

        // Update Submission values
        submissionToUpdate.updateValues(updatedValues);

        // Return updated Submission
        return ResponseEntity.status(200).body(submissionToUpdate);
    }

    // Delete Submission
    @DeleteMapping("/geohunt/submission/{id}")
    public ResponseEntity<String> deleteSubmission(@PathVariable long id){
        // Remove Submission
        submissionsRepository.deleteById(id);

        // Need to check what should be returned
        return ResponseEntity.status(200).body("submission removed.");
    }

    // List Submissions by user
    @GetMapping("/account/{uid}/submissions")
    public ResponseEntity<List<Submissions>> listSubmissionsWithUser(@PathVariable long uid){
        try{
            Account account = accountService.getAccountById(uid);
            return ResponseEntity.status(200).body(account.getSubmissions());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(null);
        }
    }

    // List Submissions by challenge
    @GetMapping("/geohunt/challenge/{cid}/submissions")
    public ResponseEntity<List<Submissions>> listSubmissionsWithChallenge(@RequestParam long cid){
        // TODO: Test with challenges code once merging.
        try{
            Challenges challenge = challengesRepository.getReferenceById(cid);
            return ResponseEntity.status(200).body(challenge.getSubmissions());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(null);
        }
    }
}
