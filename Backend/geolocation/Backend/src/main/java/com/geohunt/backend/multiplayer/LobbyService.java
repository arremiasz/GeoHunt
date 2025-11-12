package com.geohunt.backend.multiplayer;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Challenges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

@Service
public class LobbyService {

    static final String ERROR_LOBBY_NOT_FOUND = "error: lobby not found";
    static final String ERROR_NOT_LOBBY_LEADER = "error: not lobby leader";
    static final String ERROR_CANNOT_JOIN = "error: cannot join lobby";
    static final String ERROR_INCORRECT_ARGUMENT = "error: incorrect argument";

    @Autowired AccountService accountService;
    @Autowired GeohuntService geohuntService;
    @Autowired MultiplayerSocket multiplayerSocket;

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
            sendWSMessage(user, ERROR_LOBBY_NOT_FOUND);
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
            sendWSMessage(user, ERROR_CANNOT_JOIN);
            return null;
        }
    }

    // Leave Lobby
    public void leaveLobby(Account user){
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            sendWSMessage(user, ERROR_LOBBY_NOT_FOUND);
            return; // Cannot find lobby to leave
        }

        userLobbyMap.remove(user.getUsername());
        lobby.removePlayer(user);

        sendWSMessage(user, "left_lobby");
        sendWSMessage(lobby.getConnectedPlayers(), "user_left: " + user.getUsername());
    }

    // Invite users
    public void inviteUser(Account userToInvite){
        // TODO: Ask about how notifications will be used to invite users.
        // Will try to send a notification to another user with the lobby code, which the frontend will recieve and use to join the lobby.
    }

    // Change Lobby Settings
    public void changeSetting(Account user, String setting, String args){
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            sendWSMessage(user, ERROR_LOBBY_NOT_FOUND);
            return;
        }
        if(!lobby.isLobbyLeader(user)){
            sendWSMessage(user, ERROR_NOT_LOBBY_LEADER);
            return;
        }

        if(setting.equals("radius")){

            double radius;
            try{
                radius = Double.parseDouble(args);
            }
            catch(NullPointerException e){
                sendWSMessage(user, ERROR_INCORRECT_ARGUMENT);
                return;
                // Args is null
            }
            catch (NumberFormatException e){
                sendWSMessage(user, ERROR_INCORRECT_ARGUMENT);
                return;
                // Args is not a number
            }
            lobby.setRadius(radius);

        } else if (setting.equals("center")) {

            // set center

        } else if (setting.equals("powerups")) {

            // set powerups

        }
    }

    public void startGame(Account user){
        // Check user
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            sendWSMessage(user, ERROR_LOBBY_NOT_FOUND);
            return;
        }
        if(!lobby.isLobbyLeader(user)){
            sendWSMessage(user, ERROR_NOT_LOBBY_LEADER);
            return;
        }

        // Check for valid generation settings
        if(lobby.getRadius() == 0 || lobby.getRadiusCenter().isAtZero()){
            sendWSMessage(user, "error: invalid_generation_settings");
        }

        // Generate Challenge
        Challenges challenges = geohuntService.getChallenge(lobby.getRadiusCenter().getLatitude(), lobby.getRadiusCenter().getLongitude(), lobby.getRadius());
        lobby.currentChallenge = challenges; // TODO: Handle possible errors here?

        // Inform users
        StringBuilder message = new StringBuilder();
        message.append("game_start \n");
        message.append("challenge: " + challenges.getId()); // TODO: Not sure if this should just be the id or an entire JSON object
        sendWSMessage(lobby.getConnectedPlayers(), message.toString());
    }

    // send WS Messages
    public void sendWSMessage(Account user, String message){
        multiplayerSocket.sendStringToUser(user.getUsername(), message);
    }

    public void sendWSMessage(List<Account> users, String message){
        for(Account user : users){
            multiplayerSocket.sendStringToUser(user.getUsername(), message);
        }
    }

}
