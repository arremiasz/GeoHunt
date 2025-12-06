package com.geohunt.backend.rewards;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Submissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RewardService {

    @Autowired RewardRepository rewardRepository;
    @Autowired AccountService accountService;


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

    public List<Customization> getCustomizations(List<Reward> unfilteredList){
        List<Customization> filteredList = new ArrayList<>();
        for(Reward reward : unfilteredList){
            if(reward instanceof Customization){
                filteredList.add((Customization) reward);
            }
        }
        return filteredList;
        // Todo: Generalize method for different subclasses of Reward
    }

    // User inventories

    public List<Reward> getUserInventory(Account account){
        return account.getInventory();
    }

    public void addRewardToUserInventory(Account account, Reward reward){
        account.getInventory().add(reward);
    }

    public void removeRewardFromUserInventory(Account account, Reward reward){
        account.getInventory().remove(reward);
    }


    // Grade Submission and Assign Reward
    public Reward gradeSubmissionAndAssignReward(int value){
        // Return random reward using weights based on value and submission score.
        // Get submission value
        int submissionValue = value;

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
