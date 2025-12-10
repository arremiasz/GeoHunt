package com.jubair5.geohunt.reward.powerups;

import com.jubair5.geohunt.R;

public class hintPu implements PowerUp{
    private String title;
    private String description;
    private int resource;
    private int amount;


    public hintPu(){
        this.title = "Hint PowerUp";
        this.description = "Points an Arrow in the direction of the location for a limited amount of time";
        this.resource = R.drawable.hint_pu;
        this.amount = 0;
    }

    @Override
    public void activate() {

    }

    public void setAmount(int amount){
        this.amount = amount;
    }
    @Override
    public int getImage() {return resource;}

    @Override
    public int getAmount() {return amount;}

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {return description;}
}
