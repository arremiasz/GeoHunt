package com.geohunt.backend.multiplayer;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.multiplayer.exceptions.LobbyNotFoundException;
import com.geohunt.backend.multiplayer.exceptions.LobbyNotJoinableException;
import com.geohunt.backend.multiplayer.exceptions.NotLobbyLeaderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LobbyService {

    @Autowired AccountService accountService;
    @Autowired GeohuntService geohuntService;

    Map<String, Lobby> userLobbyMap = new HashMap<>();
    Map<Long, Lobby> idLobbyMap = new HashMap<>();

    // Create Lobby
    public Lobby createLobby(Account user) throws IllegalStateException{
        Lobby lobby = userLobbyMap.get(user);
        if(lobby != null){
            throw new IllegalStateException("user already in lobby");
        }

        lobby = new Lobby(user);
        userLobbyMap.put(user.getUsername(), lobby);
        idLobbyMap.put(lobby.getId(), lobby);
        return lobby;
        // TODO: Move WS Message to Socket
        // Exceptions: IllegalState (User already in lobby)
    }

    // Join Lobby by Id
    public Lobby joinLobby(Account user, Long lobbyId) throws LobbyNotFoundException, LobbyNotJoinableException {
        Lobby lobby = idLobbyMap.get(lobbyId);
        if(lobby == null){
            throw new LobbyNotFoundException();
        }

        boolean joined = lobby.addPlayer(user);
        if(!joined){
            throw new LobbyNotJoinableException();
        }

        //sendWSMessage(user, lobby.getJoinWSMessage());
        //sendWSMessage(lobby.getConnectedPlayers(), "user_joined: " + user.getUsername());
        userLobbyMap.put(user.getUsername(), lobby);
        return lobby;
        // TODO: Move WS Message to Socket using Exceptions
    }

    // Leave Lobby
    public Lobby leaveLobby(Account user) throws IllegalStateException{
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            throw new IllegalStateException("User not in lobby");
        }

        userLobbyMap.remove(user.getUsername());
        lobby.removePlayer(user);

        //sendWSMessage(user, "left_lobby");
        //sendWSMessage(lobby.getConnectedPlayers(), "user_left: " + user.getUsername());
        return lobby;
        // TODO: Move WS Message to Socket using Exceptions

    }

    // Invite users
    public void inviteUser(Account userInviting, Account userToInvite){
        // TODO: Ask about how notifications will be used to invite users.
        // Will try to send a notification to another user with the lobby code, which the frontend will recieve and use to join the lobby.
        // TODO: Move WS Message to Socket using Exceptions

    }

    // Change Lobby Settings
    public void changeSetting(Account user, String setting, String args) throws IllegalStateException, NotLobbyLeaderException, IllegalArgumentException {
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            throw new IllegalStateException("User not in lobby");
        }
        if(!lobby.isLobbyLeader(user)){
            throw new NotLobbyLeaderException();
        }

        if(setting.equals("radius")){

            double radius;
            try{
                radius = Double.parseDouble(args);
            }
            catch(NullPointerException e){
                throw new IllegalArgumentException();
                // Args is null
            }
            catch (NumberFormatException e){
                throw new IllegalArgumentException();
                // Args is not a number
            }
            lobby.setRadius(radius);

        } else if (setting.equals("center")) {

            // set center

        } else if (setting.equals("powerups")) {

            // set powerups

        }
        // TODO: Move WS Message to Socket using Exceptions

    }

    public void startGame(Account user) throws IllegalStateException, NotLobbyLeaderException, IllegalArgumentException{
        // Check user
        Lobby lobby = userLobbyMap.get(user.getUsername());
        if(lobby == null){
            throw new IllegalStateException("User not in lobby");
        }
        if(!lobby.isLobbyLeader(user)){
            throw new NotLobbyLeaderException();
        }

        // Check for valid generation settings
        if(lobby.getRadius() == 0 || lobby.getRadiusCenter().isAtZero()){
            throw new IllegalStateException("Radius and center not set");
        }

        // Generate Challenge
        Challenges challenges = geohuntService.getChallenge(lobby.getRadiusCenter().getLatitude(), lobby.getRadiusCenter().getLongitude(), lobby.getRadius());
        lobby.currentChallenge = challenges; // TODO: Handle possible errors here?

        // Inform users
        StringBuilder message = new StringBuilder();
        message.append("game_start \n");
        message.append("challenge: " + challenges.getId()); // TODO: Not sure if this should just be the id or an entire JSON object
        //sendWSMessage(lobby.getConnectedPlayers(), message.toString());
        // TODO: Move WS Message to Socket using Exceptions

    }

    public void submit(Account user) throws IllegalStateException{

    }

    public void closeLobby(Lobby lobby) throws IllegalStateException{

    }

}
