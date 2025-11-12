package com.geohunt.backend.multiplayer;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

@Service
public class LobbyService {

    @Autowired
    AccountService accountService;

    Map<String, Lobby> userLobbyMap = new HashMap<>();
    Map<Long, Lobby> idLobbyMap = new HashMap<>();

    // Create Lobby
    public Lobby createLobby(Account user){
        Lobby lobby = new Lobby(user);
        userLobbyMap.put(user.getUsername(), lobby);
        idLobbyMap.put(lobby.getId(), lobby);
        sendWSMessage(user, "created_lobby: " + lobby.getId());
        return lobby;
    }

    // Join Lobby by Id
    public Lobby joinLobby(Account user, Long lobbyId){
        Lobby lobby = idLobbyMap.get(lobbyId);
        if(lobby == null){
            sendWSMessage(user, "lobby_find_fail");
            return null; // Cannot find lobby
        }

        boolean joined = lobby.addPlayer(user);
        if(joined){
            sendWSMessage(user, lobby.getJoinWSMessage());
            sendWSMessage(lobby.getConnectedPlayers(), "user_joined: " + user.getUsername());
            userLobbyMap.put(user.getUsername(), lobby);
            return lobby;
        }
        else {
            sendWSMessage(user, "lobby_join_fail");
            return null;
        }
    }

    // Leave Lobby
    public void leaveLobby(Account user){
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            return; // Cannot find lobby to leave
        }

        userLobbyMap.remove(user.getUsername());
        lobby.removePlayer(user);

        sendWSMessage(user, "left_lobby");
        sendWSMessage(lobby.getConnectedPlayers(), "user_left: " + user.getUsername());
    }

    // Invite users
    public void inviteUser(Account userToInvite){

    }

    // Change Lobby Settings
    public void changeSetting(Account user, String setting, String args){
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            return;
        }
        if(!lobby.isLobbyLeader(user)){
            return;
        }

        if(setting.equals("radius")){
            // set radius
        } else if (setting.equals("center")) {
            // set center
        } else if (setting.equals("powerups")) {
            // set powerups
        }
    }

    public void startGame(){
        // Generate & Save Challenge

        // Inform users
    }

    // send WS Messages
    public void sendWSMessage(Account user, String message){

    }

    public void sendWSMessage(List<Account> users, String message){

    }

}
