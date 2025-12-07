package com.geohunt.backend.rewards;


import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountRepository;
import com.geohunt.backend.database.Submissions;
import com.geohunt.backend.database.SubmissionsService;
import com.geohunt.backend.images.Image;
import com.geohunt.backend.images.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RewardController {

    @Autowired RewardService rewardService;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;
    @Autowired SubmissionsService submissionsService;
    @Autowired ImageService imageService;

    // Assign Reward from Submission

    @PostMapping("account/{uid}/rewards/gradesubmission/{sid}")
    public ResponseEntity<Reward> gradeSubmission(@PathVariable long sid, @PathVariable long uid){
        try{
            Account account = accountService.getAccountById(uid);
            Submissions submissions = submissionsService.getSubmissionById(sid);

            int submissionValue = submissions.getSubmissionPoints();
            account.incrementPoints(submissionValue);

            Reward reward = rewardService.gradeSubmissionAndAssignReward(submissionValue);
            rewardService.addRewardToUserInventory(account, reward);

            return ResponseEntity.ok(reward);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // User Inventory

    @GetMapping("/account/{id}/inventory")
    public ResponseEntity<List<Reward>> getUserInventory(@PathVariable long id){
        if(accountRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Account account = accountRepository.findById(id).get();
        List<Reward> userInventory = rewardService.getUserInventory(account);
        return ResponseEntity.ok(userInventory);
    }

    @GetMapping("/account/{id}/inventory/customizations")
    public ResponseEntity<List<Customization>> getUserCustomizations(@PathVariable long id){
        if(accountRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Account account = accountRepository.findById(id).get();
        List<Reward> userInventory = rewardService.getUserInventory(account);
        List<Customization> customizationList = rewardService.getCustomizations(userInventory);
        return ResponseEntity.ok(customizationList);
    }

    // Universal

    @GetMapping("/rewards/{id}")
    public ResponseEntity<Reward> getReward(@PathVariable long id){
        Reward reward = rewardService.getReward(id);
        if(reward == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(reward);
    }

    @DeleteMapping("/rewards/{id}")
    public ResponseEntity<String> deleteReward(@PathVariable long id){
        if(rewardService.deleteReward(id)){
            return ResponseEntity.ok("Reward deleted");
        }
        else{
            return ResponseEntity.badRequest().body("Reward does not exist");
        }
    }

    @PutMapping("/rewards/{rid}/image")
    public ResponseEntity<Reward> setRewardImage(@RequestParam long imageId, @PathVariable long rid){
        Reward reward = rewardService.getReward(rid);
        Image image = imageService.getImageObj(imageId);
        if(reward == null || image == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        reward.setRewardImage(image);
        rewardService.saveReward(reward);
        return ResponseEntity.ok(reward);
    }

    // Customizations

    @PostMapping("/rewards/customizations")
    public ResponseEntity<Customization> submitCustomization(@RequestBody Customization customization){
        rewardService.saveReward(customization);
        return ResponseEntity.ok(customization);
    }


}
