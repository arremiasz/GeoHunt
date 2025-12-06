//package com.geohunt.backend.rewards;
//
//import com.geohunt.backend.Services.AccountService;
//import com.geohunt.backend.database.Submissions;
//import com.geohunt.backend.database.SubmissionsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//public class RewardController {
//
//    // Assign Reward from Submission
//    @PostMapping("/submissions/assign")
//    public ResponseEntity<Reward> assignRewardFromSubmission(@RequestParam long submissionId){
//        try{
//            Submissions submissions = submissionsService.getSubmissionById(submissionId);
//
//        }
//        catch (IllegalArgumentException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
//    }
//
//
//    // Post
//
//    // Get
//
//    // Update
//
//    // Delete
//
//    // List
//}
