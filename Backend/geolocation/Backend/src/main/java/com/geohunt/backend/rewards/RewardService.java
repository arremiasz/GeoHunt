package com.geohunt.backend.rewards;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Shop.Shop;
import com.geohunt.backend.Shop.UserInventory;
import com.geohunt.backend.Shop.UserInventoryRepository;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Submissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    @Autowired RewardRepository rewardRepository;
    @Autowired AccountService accountService;
    @Autowired UserInventoryRepository userInventoryRepository;


    // Interface with database

    public void saveReward(Reward reward){
        rewardRepository.save(reward);
    }

    public Reward getReward(long id){
        if(rewardRepository.findById(id).isPresent()){
            return rewardRepository.findById(id).get();
        }
        return null;
    }

    public boolean deleteReward(long id){
        if(rewardRepository.findById(id).isPresent()){
            rewardRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Reward> getAllRewards(){
        return rewardRepository.findAll();
    }

    // User inventories

//    public List<Reward> getUserInventory(Account account){
//        return account.getInventory();
//    }

    public void addRewardToUserInventory(Account account, Reward reward){
        Optional<UserInventory> userInventory = userInventoryRepository.findByUserIdAndShopItemId(account.getId(), reward.getShopItem().getId());

        if(userInventory.isEmpty()){
            UserInventory ui = new UserInventory();
            ui.setUser(account);
            ui.setShopItem(reward.getShopItem());
            ui.setQuantity(1);
            ui.setEquipped(false);
            ui.setAcquiredAt(new Date());
            userInventoryRepository.save(ui);
        } else {
            UserInventory inv = userInventory.get();
            inv.setQuantity(inv.getQuantity() + 1);
            userInventoryRepository.save(inv);
        }
        // Changed to add the shop item to the user inventory
    }


    // Grade Submission and Assign Reward
    public Reward gradeSubmissionAndAssignReward(int submissionValue){
        // Return random reward using weights based on value and submission score.
        // Get submission value

        // Get reward list
        Reward[] rewards = getAllRewards().toArray(new Reward[0]);
        double[] weights = new double[rewards.length];

        // For each, get weight and add to array
        for(int i = 0; i < rewards.length; i++){
            weights[i] = getRewardWeight(submissionValue, rewards[i].getValue());
        }

        // Get random index
        int randIndex = chooseRandom(weights);

        // Return given reward
        Reward output = rewards[randIndex];
        return output;
    }

    private double getRewardWeight(int submissionValue, int rewardValue){
        double valueDifference = Math.abs(submissionValue - rewardValue);
        double weight = 0.5 * Math.exp(-1*valueDifference/270); //weight fluctuates from about 0.5 to 0.01
        return weight;
    }

    private int chooseRandom(double[] weights){
        double totalWeight = 0;
        for(double weight : weights){
            totalWeight += weight;
        }

        double random = totalWeight*Math.random();
        double totalAtIndex = 0;
        int i = 0;

        while (i < weights.length-1 && totalAtIndex < random){
            totalAtIndex += weights[i];
            i++;
        }

        return i;
    }
}
