package com.geohunt.backend.rewards;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Submissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RewardService {
    @Autowired
    RewardRepository rewardRepository;

    // Assign Reward from Submission
    public Reward assignReward(Submissions submission){
        // Return random reward with a higher weight to rewards with a similar point value.
        int submissionValue = submission.getSubmissionPoints();
    }

    // Save

    public long saveReward(Reward reward){
        rewardRepository.save(reward);
        return reward.getId();
    }

    // Get

    public Reward getRewardById(Long id){
        Optional<Reward> rewardOptional = rewardRepository.findById(id);
        if(rewardOptional.isEmpty()){
            return null;
        }
        return rewardOptional.get();
    }

    public List<Reward> getRewardsByAccount(Account owner){
        return owner.getRewards();
    }

    // Update

    public Reward updateReward(Long id, Reward rewardNew){
        Reward reward = getRewardById(id);
        if(reward == null){
            return null;
        }

        reward.update(rewardNew);

        rewardRepository.save(reward);

        return reward;

    }

    // Delete

    public void removeReward(Reward reward){
        rewardRepository.delete(reward);
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
