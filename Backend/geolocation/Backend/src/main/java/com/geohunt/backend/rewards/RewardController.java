package com.geohunt.backend.rewards;


import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Shop.Shop;
import com.geohunt.backend.Shop.ShopRepository;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Submissions;
import com.geohunt.backend.database.SubmissionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RewardController {

    @Autowired RewardService rewardService;
    @Autowired AccountService accountService;
    @Autowired SubmissionsService submissionsService;
    @Autowired ShopRepository shopRepository;

    // Game Endpoints

    @Operation(summary = "Grade submission and receive reward")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receive Reward", content = @Content),
            @ApiResponse(responseCode = "400", description = "Submission has already been graded", content = @Content),
            @ApiResponse(responseCode = "404", description = "Couldn't find user or submission", content = @Content)
    })
    @GetMapping("/gradesubmission")
    public ResponseEntity<SubmissionRewardDTO> gradeSubmission(@RequestParam long sid, @RequestParam long uid){
        try{
            Account account = accountService.getAccountById(uid);
            Submissions submissions = submissionsService.getSubmissionById(sid);

            if(submissions.isHasGeneratedReward()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            int submissionValue = submissions.getSubmissionPoints();
            account.incrementPoints(submissionValue);

            Reward reward = rewardService.gradeSubmissionAndAssignReward(submissionValue);
            rewardService.addRewardToUserInventory(account, reward);

            submissions.setHasGeneratedReward(true);

            SubmissionRewardDTO data = new SubmissionRewardDTO();
            data.setReward(reward);
            data.setSubmissionValue(submissionValue);

            return ResponseEntity.ok(data);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

//    @Operation(summary = "Get user inventory")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Receive list of items in user inventory", content = @Content),
//            @ApiResponse(responseCode = "404", description = "Couldn't find user", content = @Content)
//    })
//    @GetMapping("/account/{id}/inventory")
//    public ResponseEntity<List<Reward>> getUserInventory(@PathVariable long id){
//        if(accountRepository.findById(id).isEmpty()){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        Account account = accountRepository.findById(id).get();
//        List<Reward> userInventory = rewardService.getUserInventory(account);
//        return ResponseEntity.ok(userInventory);
//    }

    @Operation(summary = "Get specific reward by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get reward object", content = @Content),
            @ApiResponse(responseCode = "404", description = "Couldn't find reward object", content = @Content)
    })
    @GetMapping("/rewards/{id}")
    public ResponseEntity<Reward> getReward(@PathVariable long id){
        Reward reward = rewardService.getReward(id);
        if(reward == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(reward);
    }

    // Dev Endpoints

    @DeleteMapping("/rewards/{id}")
    public ResponseEntity<String> deleteReward(@PathVariable long id){
        if(rewardService.deleteReward(id)){
            return ResponseEntity.ok("Reward deleted");
        }
        else{
            return ResponseEntity.badRequest().body("Reward does not exist");
        }
    }

    @PostMapping("/rewards")
    public ResponseEntity<Reward> createRewardFromShopItem(@RequestParam long shopId){
        Optional<Shop> optItem = shopRepository.findById(shopId);
        if(optItem.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Shop item = optItem.get();
        Reward reward = new Reward();
        reward.setShopItem(item);
        rewardService.saveReward(reward);
        return ResponseEntity.ok(reward);
    }
}
