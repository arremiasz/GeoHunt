package com.jubair5.geohunt.reward.powerups;

import com.jubair5.geohunt.R;

public class LargeTimeReductionPU implements PowerUp{
    private String title;
    private String description;
    private int resource;
    private int amount;


    public LargeTimeReductionPU(){
        title = "Large Time Reduction PowerUp";
        description = "Reduces the time of the current make by 5 minutes";
        resource = R.drawable.large_time_reduction;
        amount = 0;
    }

    @Override
    public void activate() {

    }

    @Override
    public int getImage() {return resource;}

    @Override
    public int getAmount() {return amount;}

    @Override
    public void setAmount(int amount) {this.amount = amount;}

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
