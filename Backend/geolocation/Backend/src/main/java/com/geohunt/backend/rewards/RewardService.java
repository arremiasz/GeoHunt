package com.geohunt.backend.rewards;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Submissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    @Autowired RewardRepository rewardRepository;


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

    public List<Customization> filterRewardsByCustomization(List<Reward> unfilteredList){
        List<Customization> filteredList = new ArrayList<>();
        for(Reward reward : unfilteredList){
            if(reward instanceof Customization){
                filteredList.add((Customization) reward);
            }
        }
        return filteredList;
        // Todo: Generalize method for different subclasses of Reward
    }


    // Assign Reward from Submission
    public Reward assignReward(Submissions submission){
        // Return random reward with a higher weight to rewards with a similar point value.
        int submissionValue = submission.getSubmissionPoints();
        return null;
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

        double random = Math.random();
        double totalAtIndex = 0;
        int i = 0;

        while (i < weights.length && totalAtIndex < random){
            totalAtIndex += weights[i];
            i++;
        }

        return i;
    }
}
