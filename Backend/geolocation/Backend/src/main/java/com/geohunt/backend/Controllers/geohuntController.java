package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Challenges> getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
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
    public ResponseEntity<Challenges> getChallengeByID(@RequestParam long id) {
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
    public ResponseEntity deleteChallengeByID(@RequestParam long id) {
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
    public ResponseEntity createChallenge(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
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
    public ResponseEntity randChallenge(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        List<Challenges> l = geohuntService.fallbackGenerate(lat, lng, radius, 1);
        return ResponseEntity.ok().body(l.get(0));
    }


    @Operation(summary = "Upload custom challenge by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content Uploaded",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenges.class))),
            @ApiResponse(responseCode = "500", description = "User not found.", content = @Content)})
    @PostMapping("/geohunt/customChallenge")
    public ResponseEntity customChallenge(@RequestParam double lat, @RequestParam double lng, @RequestParam long uid, @RequestBody String url){
        return geohuntService.customChallenge(lat, lng, uid, url);
    }

    @Operation(summary = "Get users own submitted challenges")
    @GetMapping("/geohunt")
    public ResponseEntity getMyChallenges(@RequestParam long id) {
        return geohuntService.getUsersChallenges(id);
    }


    @Operation(summary = "Delete users challenge by challenge id. Challenge id has to belong to creator.")
    @DeleteMapping("/geohunt/mySubmissions")
    public ResponseEntity<String> deleteMySubmissions(@RequestParam long userId, @RequestParam long chalId){return geohuntService.deleteUsersChallenges(userId, chalId);}

    @Operation(summary = "Update users own submitted id")
    @PutMapping("/geohunt/mySubmission/updateChallenge")
    public ResponseEntity updateMyChallenge(@RequestParam long userId, @RequestParam long chalId, @RequestParam double lat, @RequestParam double lng, @RequestBody String image){
        return geohuntService.updateChallenge(userId, chalId, image, lng, lat);
    }
}
