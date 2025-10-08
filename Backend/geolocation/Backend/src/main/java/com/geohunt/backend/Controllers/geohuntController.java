package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Submissions;
import com.geohunt.backend.database.SubmissionsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class geohuntController {

    SubmissionsRepository submissionsRepository;

    @GetMapping("/geohunt/getLocation")
    public String getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        return "No";
    }

    // Receive Submission
    @PostMapping("/geohunt/submission")
    public ResponseEntity<Submissions> receiveSubmission(@RequestBody Submissions submission){
        // Verify Submission
//        if(!submission.verifySubmission()){
//            return ResponseEntity.status(400).body("Submission does not have required information");
//        }

        // Add Submission to Database
//        submissionsRepository.save(submission);

        return ResponseEntity.status(200).body(submission);
    }

    // Get Submission Info
    @GetMapping("/geohunt/submission/{Id}")
    public ResponseEntity<Submissions> getSubmission(@PathVariable long id){
        if(!submissionsRepository.findById(id).isPresent()){
            // No submission with given id exists.
            return ResponseEntity.status(404).body(null);
        }
        Submissions submission = submissionsRepository.findById(id).get();
        return ResponseEntity.status(200).body(submission);
    }
}
