package com.geohunt.backend.rewards;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RewardController {

    @Autowired
    RewardService rewardService;

    // Assign Reward from Submission


    // Universal

    @GetMapping("/rewards/{id}")
    public ResponseEntity<Reward> getCustomization(@PathVariable long id){
        Reward reward = rewardService.getReward(id);
        if(reward == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(reward);
    }

    // Customizations

    @PostMapping("/rewards/customizations")
    public ResponseEntity<Customization> submitCustomization(@RequestBody Customization customization){
        rewardService.saveReward(customization);
        return ResponseEntity.ok(customization);
    }


}
