package com.geohunt.backend.multiplayer;


import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Account;
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
import java.util.Map;

@Controller
@ServerEndpoint("/multiplayer/{username}")
public class MultiplayerSocket {

    public static AccountService accountService;
    public static LobbyService lobbyService;

    @Autowired
    public void setAccountService(AccountService service){ accountService = service; }
    @Autowired
    public void setLobbyService(LobbyService service){lobbyService = service;}

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

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
        sendStringToUser(username, "Connection Success");
        logger.info("OnOpen : User " + username + " connected");
    }

    @OnMessage
    public void onMessage(Session session, String message){
        // If the user is not in a lobby, they can create or join one.
        // If a user is in a lobby, the request is handled by the lobby.
        logger.info("OnMessage : Got Message: " + message);
        String username = sessionUsernameMap.get(session);
        Account user = accountService.getAccountByUsername(username);
        String[] splitMsg = message.split("[,:]"); // splits incoming messages by : or ,

        // Handle messages
        if(splitMsg[0].equals("create lobby")) {
            lobbyService.createLobby(user);
        }
        else if (splitMsg[0].equals("join lobby")){
            String args = splitMsg[1];
            //lobbyService.joinLobby(user, args); // TODO: Parse args as long
        }
        else if (splitMsg[0].equals("leave lobby")){
            lobbyService.leaveLobby(user);
        }
        else if (splitMsg[0].equals("invite user")){
            String args = splitMsg[1];
            Account userToInvite = accountService.getAccountByUsername(args);
            lobbyService.inviteUser(user, userToInvite);
        }
        else if (splitMsg[0].equals("change setting")){
            String setting = splitMsg[1];
            // TODO: Create args list
        }
        else if (splitMsg[0].equals("start game")){
            lobbyService.startGame(user);
        }
        else if (splitMsg[0].equals("submit")){
            // TODO: Handle Submission JSON object
        }

    }

    @OnClose
    public void onClose(Session session){
        // Remove player from lobby.
        String username = sessionUsernameMap.get(session);
        Account user = accountService.getAccountByUsername(username);
        logger.info("OnClose : User " + username + " disconnected");

        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        lobbyService.leaveLobby(user);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("OnError : Entered into Error");
        throwable.printStackTrace();
    }


    // Sending Messages

    public void sendStringToUser(String username, String message){
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
