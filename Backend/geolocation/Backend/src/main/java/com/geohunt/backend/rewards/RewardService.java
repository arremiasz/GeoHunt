package com.geohunt.backend.rewards;

import com.geohunt.backend.database.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RewardService {
    @Autowired
    RewardRepository rewardRepository;

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
        // TODO: Link Accounts with Rewards
        return null;
    }

    // Update

    public Reward updateReward(Long id, Reward rewardNew){
        Reward rewardOld = getRewardById(id);
        if(rewardOld == null){
            return null;
        }

        // TODO: Update function

        return null;

    }

    // Delete

    public void removeReward(Reward reward){
        rewardRepository.delete(reward);
    }
}
