package com.geohunt.backend.multiplayer;


import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lobby {

    static final int MAX_PLAYERS = 8;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    // Services
    @Autowired
    AccountService accountService;

    // Connected Players
    List<Account> connectedPlayers;
    Account lobbyLeader;

    // Settings
    double radius;
    Location radiusCenter;
    boolean powerUpsEnabled;

    public Lobby(String leaderUsername){
        connectedPlayers = new ArrayList<>();
        lobbyLeader = accountService.getAccountByUsername(leaderUsername);
        connectedPlayers.add(lobbyLeader);
    }

    public void joinLobby(String username){
        Account player = accountService.getAccountByUsername(username);
        connectedPlayers.add(player);
    }

    public void disconnectUser(String username){
        Account player = accountService.getAccountByUsername(username);
        connectedPlayers.remove(player);
    }

    // Processes lobby-specific requests from websocket onMessage() method
    public void processMessage(String string){

    }
}
