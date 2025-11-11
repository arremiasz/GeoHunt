package com.geohunt.backend.multiplayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LobbyService {

    @Autowired
    MultiplayerSocket multiplayerSocket;
    Map<String, Lobby> lobbyMap = new HashMap<>();

    // Create Lobby
    public Lobby createLobby(String username){

    }

    // Join Lobby by Id
    public Lobby joinLobby(String username, String lobbyId){

    }

    // Leave Lobby
    public void leaveLobby(String username){

    }

    // Invite users
    public void inviteUser(String username){

    }

    // Change Lobby Settings
    public void changeSetting(String setting, String args){

    }

    // send WS Messages
    public void sendWSMessage(String username, String message){

    }

}
