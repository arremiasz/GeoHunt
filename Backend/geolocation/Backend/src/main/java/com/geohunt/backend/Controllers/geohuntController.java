package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Submissions;
import com.geohunt.backend.database.SubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class geohuntController {

    @Autowired
    SubmissionsRepository submissionsRepository;

    @GetMapping("/geohunt/getLocation")
    public String getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        return "No";
    }

    // Post Submission
    @PostMapping("/geohunt/submission")
    public ResponseEntity<Submissions> receiveSubmission(@RequestBody Submissions submission){
        // Verify Submission
//        if(!submission.verifySubmission()){
//            return ResponseEntity.status(400).body(null);
//        }

        // Add Submission to Database
        submissionsRepository.save(submission);


        return ResponseEntity.status(200).body(submission);
    }

    // Get Submission
    @GetMapping("/geohunt/submission/{id}")
    public ResponseEntity<Submissions> getSubmission(@PathVariable long id){
        if(submissionsRepository.findById(id).isEmpty()){
            // No submission with given id exists.
            return ResponseEntity.status(404).body(null);
        }
        Submissions submission = submissionsRepository.findById(id).get();
        return ResponseEntity.status(200).body(submission);
    }

    // Put / Update Submission
    @PutMapping("/geohunt/submission/{id}")
    public ResponseEntity<Submissions> updateSubmission(@RequestBody Submissions submission, @PathVariable long id){
        // Get Submission with Id

        // Update Submission values

        // Return updated Submission
        return null;
    }

    // Delete Submission
    @DeleteMapping("/geohunt/submission/{id}")
    public ResponseEntity<String> deleteSubmission(@PathVariable long id){
        // Get Submission with Id

        // Remove Submission

        // Need to check what should be returned
        return null;
    }
}
