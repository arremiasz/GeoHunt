package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author evan juslon
 */
@Tag(name = "Submission Handler", description = "Operations related to submissions to challenges")
@RestController
public class SubmissionController {

    @Autowired
    SubmissionsService submissionsService;

    @Operation(summary = "Submit submission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission success, returns distance in miles", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))
            }),
            @ApiResponse(responseCode = "400", description = "Submission failed, returns distance equal to -1", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))
            })
    })
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

    @Operation(summary = "Get submission by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Submissions.class))
            }),
            @ApiResponse(responseCode = "404", description = "Cannot find submission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Submissions.class))
            })
    })
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

    @Operation(summary = "Update submission values by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission found and values updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Submissions.class))
            }),
            @ApiResponse(responseCode = "400", description = "Cannot find and update submission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Submissions.class))
            })
    })
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

    @Operation(summary = "Delete submission by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submission found and deleted", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "404", description = "Submission wasn't found, and couldn't be deleted", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            })
    })
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

    @Operation(summary = "List submissions by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submissions found, returns list of submissions", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
            }),
            @ApiResponse(responseCode = "404", description = "No submissions found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
            })
    })
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

    @Operation(summary = "List submissions by challenge id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Submissions found, returns list of submissions", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
            }),
            @ApiResponse(responseCode = "404", description = "No submissions found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
            })
    })
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
