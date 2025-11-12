package com.geohunt.backend.multiplayer;


import com.geohunt.backend.database.Account;
import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.Submissions;
import com.geohunt.backend.util.Location;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import javax.tools.JavaFileManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
class Lobby {

    public static final int MAX_PLAYERS = 8;

    @Id
    private long id;

    // Connected Players
    List<Account> connectedPlayers = new ArrayList<>();
    Account lobbyLeader;

    // Settings
    double radius;
    Location radiusCenter;
    boolean powerUpsEnabled;

    // Match specific
    Challenges currentChallenge;
    Submissions submissions;

    // Status
    boolean canJoin;
    boolean open;

    Lobby(Account lobbyLeader){
        addPlayer(lobbyLeader);
        setLobbyLeader(lobbyLeader);
        canJoin = true;
        open = true;
    }

    boolean addPlayer(Account account){
        if(connectedPlayers.contains(account)){
            return false; // Player already in lobby.
        }
        if(connectedPlayers.size() >= MAX_PLAYERS){
            return false; // Lobby full
        }

        connectedPlayers.add(account);
        return true;
    }

    void removePlayer(Account account){
        if(!connectedPlayers.contains(account)){
            return; // Player is not in the lobby.
        }

         connectedPlayers.remove(account);
    }

    void setLobbyLeader(Account account){
        if(!connectedPlayers.contains(account)){
            return; // Player must be in the lobby to become lobby leader
        }

        lobbyLeader = account;
    }

    String getUserListToString(){
        if(connectedPlayers.isEmpty()){
            return "";
        }
        StringBuilder out = new StringBuilder();
        for(Account account : connectedPlayers){
            out.append(account.getUsername()).append(",");
        }
        out = new StringBuilder(out.substring(0, out.length() - 1));
        return out.toString();
    }

    boolean hasUser(Account account){
        return connectedPlayers.contains(account);
    }

    boolean isLobbyLeader(Account account){
        if(lobbyLeader == null){
            return false;
        }
        return lobbyLeader.equals(account);
    }

    String getJoinWSMessage(){
        StringBuilder message = new StringBuilder();
        message.append("lobby_data \n");
        for(Account account : connectedPlayers){
            message.append("user_in_lobby: " + account.getUsername() + "\n");
        }
        message.append("lobby_leader: " + lobbyLeader.getUsername() + "\n");
        message.append("radius_center: " + radiusCenter.getLatitude() + ", " + radiusCenter.getLongitude() + "\n");
        message.append("radius: " + radius + "\n");

        return message.toString();
    }
}
