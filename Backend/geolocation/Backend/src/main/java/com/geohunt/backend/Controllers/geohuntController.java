package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Arjava Tripathi
 */
@Tag(name = "Challenge Management", description = "Operations related to challenges")
@RestController
public class geohuntController {

    @Autowired
    GeohuntService geohuntService;

    @Autowired
    ChallengesRepository challengesRepository;

    @Operation(summary = "Get a random challenge in the given latitude, longitude, and range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge retrieved from database",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenges.class)) }),
            @ApiResponse(responseCode = "500", description = "Challenge unable to be retrieved",content = @Content)})
    @GetMapping("/geohunt/getLocation")
    public ResponseEntity<Challenges> getLocation(@Parameter(description = "latitude") @RequestParam double lat, @Parameter(description = "longitude") @RequestParam double lng, @Parameter(description = "radius") @RequestParam double radius) {
        try {
            Challenges c = geohuntService.getChallenge(lat, lng, radius);
            return ResponseEntity.ok().body(c);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get a random challenge by challenge id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge retrieved from database",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenges.class)) }),
            @ApiResponse(responseCode = "404", description = "Challenge ID not found",content = @Content)})
    @GetMapping("/geohunt/getChallengeByID")
    public ResponseEntity<Challenges> getChallengeByID(@Parameter(description = "challenges id") @RequestParam long id) {
        try {
            Optional<Challenges> c = challengesRepository.findById(id);
            return ResponseEntity.ok().body(c.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @Operation(summary = "Delete a challenge by challenge id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content Deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Content not found", content = @Content)})
    @DeleteMapping("/geohunt/deleteByID")
    public ResponseEntity deleteChallengeByID(@Parameter(description = "challenge id") @RequestParam long id) {
        try {
            challengesRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Generate a new challenge in the radius, latitude, and longitude given using custom generation logic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenges.class))),
            @ApiResponse(responseCode = "500", description = "Content not created", content = @Content)})
    @PostMapping("/geohunt/createChallenge")
    public ResponseEntity createChallenge(@Parameter(description = "latitude") @RequestParam double lat, @Parameter(description = "latitude") @RequestParam double lng, @Parameter(description = "radius") @RequestParam double radius) {
        try {
            List<Challenges> c = geohuntService.generateChallenges(lat, lng, radius, 1);
            return ResponseEntity.ok().body(c.get(c.size() - 1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Generate a random challenge in the radius, latitude, and longitude given using random generation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenges.class)))})
    @PostMapping("/geohunt/randomChallenge")
    public ResponseEntity randChallenge(@Parameter(description = "latitude") @RequestParam double lat, @Parameter(description = "longitude") @RequestParam double lng, @Parameter(description = "radius") @RequestParam double radius) {
        List<Challenges> l = geohuntService.fallbackGenerate(lat, lng, radius, 1);
        return ResponseEntity.ok().body(l.get(0));
    }


    @Operation(summary = "Upload custom challenge by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content Uploaded",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenges.class))),
            @ApiResponse(responseCode = "500", description = "User not found.", content = @Content)})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "base64 string of image", required = true)
    @PostMapping("/geohunt/customChallenge")
    public ResponseEntity customChallenge(@Parameter(description = "latitude") @RequestParam double lat, @Parameter(description = "longitude") @RequestParam double lng, @Parameter(description = "users Id") @RequestParam long uid, @RequestBody String url){
        return geohuntService.customChallenge(lat, lng, uid, url);
    }

    @Operation(summary = "Get users own submitted challenges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Challenges.class)))),
            @ApiResponse(responseCode = "500", description = "User not found.", content = @Content)})
    @GetMapping("/geohunt")
    public ResponseEntity getMyChallenges(@Parameter(description = "users Id") @RequestParam long id) {
        return geohuntService.getUsersChallenges(id);
    }


    @Operation(summary = "Delete users challenge by challenge id. Challenge id has to belong to creator.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content Deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content)})
    @DeleteMapping("/geohunt/mySubmissions")
    public ResponseEntity<String> deleteMySubmissions(@Parameter(description = "users Id") @RequestParam long userId, @Parameter(description = "challenges Id") @RequestParam long chalId){return geohuntService.deleteUsersChallenges(userId, chalId);}

    @Operation(summary = "Update users own submitted id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content Updated",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found, Challenge not found, or User does not own challenge", content = @Content)})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "base64 string of image", required = false)
    @PutMapping("/geohunt/mySubmission/updateChallenge")
    public ResponseEntity updateMyChallenge(@Parameter(description = "users Id") @RequestParam long userId, @Parameter(description = "challenge Id") @RequestParam long chalId, @Parameter(description = "latitude") @RequestParam double lat, @Parameter(description = "longitude") @RequestParam double lng, @RequestBody String image){

        return geohuntService.updateChallenge(userId, chalId, image, lng, lat);
    }

    // Rate Challenges
    @Operation(summary = "Add a rating to a Challenge using Challenge id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating added.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Rating not within acceptable bounds.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found.", content = @Content)})
    @PostMapping("/geohunt/rate")
    public ResponseEntity<String> submitChallengeRating(@RequestParam long cid, @RequestParam int rating){
        // Get challenge
        if(challengesRepository.findById(cid).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Challenge Not Found");
        }
        Challenges challenge = challengesRepository.findById(cid).get();

        // Verify rating is within bounds (1 - 5)
        if(rating < 1 || rating > 5){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rating Not Within Acceptable Bounds");
        }

        // add rating to challenge
        challenge.addRating(rating);
        challengesRepository.save(challenge);
        return ResponseEntity.status(HttpStatus.OK).body("Rating Added");
    }

    @Operation(summary = "Get challenge rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating processed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found.", content = @Content)})
    @GetMapping("/geohunt/challenge/{cid}/rating")
    public ResponseEntity<Double> submitChallengeRating(@RequestParam long cid){
        // Get challenge
        if(challengesRepository.findById(cid).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Challenges challenge = challengesRepository.findById(cid).get();

        List<Integer> ratings = challenge.getChallengeRatings();
        int sum = 0;
        for(Integer rating : ratings){
            sum += rating;
        }
        double avgRating = (double)sum/ratings.size();

        return ResponseEntity.status(HttpStatus.OK).body(avgRating);
    }
}
