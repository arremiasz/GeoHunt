package com.geohunt.backend.multiplayer;


import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
import jakarta.persistence.Lob;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Controller
@ServerEndpoint("/multiplayer/{username}")
public class MultiplayerSocket {

    public static AccountService accountService;

    @Autowired
    public void setAccountService(AccountService service){ accountService = service; }

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private static Map<String, Lobby> usernameLobbyMap = new HashMap<>();
    private static Map<Long, Lobby> idLobbyMap = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(MultiplayerSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException{
        // User connects to the server and is ready to join or create a lobby
        logger.info("Entered into Open");

        // Check for valid username
        try {
            accountService.getAccountByUsername(username);
        }
        catch (IllegalArgumentException e){
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid username"));
            // User has tried to connect with a username that does not exist, and should fail to connect.
        }

        // Store connecting user information
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        // Send confirmation message
        sendStringToSingleUser(username, "Connection Success");
        logger.info("OnOpen : User " + username + " connected");
    }

    @OnMessage
    public void onMessage(Session session, String message){
        // If the user is not in a lobby, they can create or join one.
        // If a user is in a lobby, the request is handled by the lobby.
        logger.info("OnMessage : Got Message: " + message);
        String username = sessionUsernameMap.get(session);

        sendStringToSingleUser(username, "Received: " + message);

        String[] splitMsg = message.split("\\s+");

        if(splitMsg.length == 0){
            // message is empty, do nothing
            return;
        }
        else if (splitMsg[0].equals("create")) {
            // message starts with "create" - create lobby
            createLobby(username);
        }
        else if (splitMsg[0].equals("join")){
            long lobbyId = Long.parseLong(splitMsg[1]);
            if(idLobbyMap.get((Long)lobbyId) == null){
                sendStringToSingleUser(username,"cannot join lobby");
                return;
            }
            joinLobby(username,lobbyId);
        }
        else if (splitMsg[0].equals("leave")){
            leaveLobby(username);
            sendStringToSingleUser(username, "Left lobby");
        }
        else if (splitMsg[0].equals("lobby")){
            Lobby lobby = usernameLobbyMap.get(username);
            lobby.processMessage(username,splitMsg);
        }
    }

    @OnClose
    public void onClose(Session session){
        // Remove player from lobby.
        String username = sessionUsernameMap.get(session);
        logger.info("OnClose : User " + username + " disconnected");
        leaveLobby(username);

        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        usernameLobbyMap.remove(username);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("OnError : Entered into Error");
        throwable.printStackTrace();
    }

    // Lobby Management

    public void createLobby(String username){
        if(usernameLobbyMap.get(username) != null){
            return;
        }
        Lobby lobby = new Lobby(accountService.getAccountByUsername(username), this, accountService);
        usernameLobbyMap.put(username, lobby);
        idLobbyMap.put((Long)lobby.getId(), lobby);
        sendStringToSingleUser(username, "Successfully created lobby " + lobby.getId());
        logger.info("Created Lobby: " + lobby.getId() + " Leader: " + username);
    }

    public void joinLobby(String username, long id){
        Lobby lobby = idLobbyMap.get((Long)id);
        lobby.joinLobby(accountService.getAccountByUsername(username));
    }

    public void leaveLobby(String username){
        Lobby lobby = usernameLobbyMap.get(username);
        if(lobby != null){
            lobby.disconnectUser(username);
            usernameLobbyMap.remove(username);
            logger.info("User " + username + " left lobby");
        }
    }

    public void closeLobby(Lobby lobby){
        List<Account> lobbyPlayerList = lobby.getConnectedPlayers();
        for(Account player : lobbyPlayerList){
            lobby.disconnectUser(player.getUsername());
            usernameLobbyMap.put(player.getUsername(), null);
        }
    }

    // Sending Messages

    public void sendStringToUserList(List<String> usernames, String message){
        for(String username : usernames) {
            sendStringToSingleUser(username, message);
        }
    }

    public void sendStringToSingleUser(String username, String message){
        Session session = usernameSessionMap.get(username);
        sendStringToSession(session, message);
    }

    public void sendStringToSession(Session session, String message){
        try {
            session.getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }
}
