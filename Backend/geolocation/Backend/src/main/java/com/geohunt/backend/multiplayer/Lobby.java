package com.geohunt.backend.multiplayer;


import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
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
import java.util.List;

@Getter
@Setter
public class Lobby {

    static final int MAX_PLAYERS = 8;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

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

    public Lobby(Account account, MultiplayerSocket socket, AccountService accountService){
        connectedPlayers = new ArrayList<>();
        lobbyLeader = account;
        connectedPlayers.add(lobbyLeader);

        multiplayerSocket = socket;
        this.accountService = accountService;
    }

    public void joinLobby(Account account){
        if(connectedPlayers.size() > MAX_PLAYERS){
            multiplayerSocket.sendStringToSingleUser(account.getUsername(), "Lobby full.");
            return;
        }
        Account player = account;
        connectedPlayers.add(player);
        sendWSMessageToAllUsers("User " + player.getUsername() + " joined the lobby");
    }

    public void disconnectUser(String username){
        Account player = accountService.getAccountByUsername(username);
        connectedPlayers.remove(player);
    }

    // Processes lobby-specific requests from websocket onMessage() method
    public void processMessage(String username, String[] splitMsg){

        String command = splitMsg[1];

        if(command.equals("edit")){
            editValue(splitMsg);
        }
        else if (command.equals("invite")){
            String userToInvite = splitMsg[2];
        }
        else if (command.equals("kick")){
            String userToKick = splitMsg[2];
        }
        else if (command.equals("match")){
            processMatch(username, splitMsg);
        }
    }

    public void processMatch(String username, String[] splitMsg){
        String command = splitMsg[2];

        if(command.equals("start")){

        }
        else if(command.equals("end")){

        }
        else if(command.equals("submit")){

        }
    }

    public void editValue(String[] splitMsg){
        String property = splitMsg[2];
        if(property.equals("radius")){
            String value = splitMsg[3];
            radius = Double.parseDouble(value);
        }
        else if (property.equals("center")){
            double latitude = Double.parseDouble(splitMsg[3]);
            double longitude = Double.parseDouble(splitMsg[4]);
        }
        else if (property.equals("powerups")){

        }
    }

    public void inviteUser(String userToInvite){

    }

    public void kickUser(String userToKick){

    }

    public void startMatch(){
        // Generate Challenge

        // Assign Challenge

        // send message to clients
    }

    public void endMatch(){
        // save challenge and submissions

        // send message to clients
    }

    private void sendWSMessageToAllUsers(String message){
        for(Account account : connectedPlayers){
            multiplayerSocket.sendStringToSingleUser(account.getUsername(), message);
        }
    }
}
