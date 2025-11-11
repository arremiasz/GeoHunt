package com.geohunt.backend.multiplayer;


import com.geohunt.backend.database.Account;
import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.Submissions;
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
import javax.xml.stream.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Lobby {

    static final int MAX_PLAYERS = 8;

    @Id
    private String id;

    // Services
    AccountService accountService;

    MultiplayerSocket multiplayerSocket;

    // Connected Players
    List<Account> connectedPlayers;
    Account lobbyLeader;

    // Settings
    double radius;
    Location radiusCenter;
    boolean powerUpsEnabled;

    // Match specific
    Challenges currentChallenge;
    Submissions submissions;
}
