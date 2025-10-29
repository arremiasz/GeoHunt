package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
import com.geohunt.backend.database.Submissions;
import com.geohunt.backend.database.SubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubmissionController {

    @Autowired
    SubmissionsRepository submissionsRepository;

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
        submission.setChallenge(null); // Challenges not implemented yet.

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
    @GetMapping("/geohunt/submission")
    public ResponseEntity<List<Submissions>> listSubmissionsWithUser(@RequestParam long uid){
        try{
            Account account = accountService.getAccountById(uid);
            return ResponseEntity.status(200).body(account.getSubmissions());
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(null);
        }
    }

    // List Submissions by challenge
    @GetMapping("/geohunt/submission")
    public ResponseEntity<List<Submissions>> listSubmissionsWithChallenge(@RequestParam long cid){
        // Challenges not implemented yet.
        return ResponseEntity.status(501).body(null);
    }
}
