package com.geohunt.backend.multiplayer;

import lombok.Getter;
import lombok.Setter;

class Leaderboard {

    private LeaderboardEntry[] entries;

    @Getter
    @Setter
    private class LeaderboardEntry{
        String username;
        long score;

        LeaderboardEntry(String username, long score){
            this.username = username;
            this.score = score;
        }
    }

    public Leaderboard(int size){
        entries = new LeaderboardEntry[size];
    }

    private void sort(){

    }
}
