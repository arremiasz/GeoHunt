package com.geohunt.backend.multiplayer;


import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.xml.stream.Location;
import java.util.List;

public class Lobby {

    static final int MAX_PLAYERS = 8;

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

    public Lobby(){

    }
}
