package com.geohunt.backend.multiplayer;


import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.multiplayer.exceptions.LobbyNotFoundException;
import com.geohunt.backend.multiplayer.exceptions.LobbyNotJoinableException;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
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
        sendWSMessage(username, "Connection Success");
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
            Lobby lobby;
            try{
                lobby = lobbyService.createLobby(user);
            }
            catch (IllegalStateException e){
                sendWSMessage(username, "CANNOT_CREATE");
                return;
            }
            sendWSMessage(username, "LOBBY_CREATED: " + lobby.getId());
            logger.info("LOBBY_CREATED: " + lobby.getId() + " BY " + username);
            return;

        }
        else if (splitMsg[0].equals("join lobby")){
            // join lobby: lobbyId

            String lobbyCode = splitMsg[1];
            Long lobbyId = Long.parseLong(lobbyCode);

            Lobby lobby;
            try{
                lobby = lobbyService.joinLobby(user, lobbyId);
            }
            catch (LobbyNotFoundException e){
                // Lobby could not be found
                sendWSMessage(username, "LOBBY_NOT_FOUND");
                return;
            }
            catch (LobbyNotJoinableException e) {
                // Lobby is not joinable
                sendWSMessage(username, "LOBBY_NOT_JOINABLE");
                return;
            }
            sendWSMessage(lobby.getConnectedPlayers(), "USER_JOINED: " + username);
            sendWSMessage(user, lobby.getJoinWSMessage());
            //TODO: Send WS Message to group and individual
        }
        else if (splitMsg[0].equals("leave lobby")){
            Lobby lobby;
            try{
                lobby = lobbyService.leaveLobby(user);
            } catch (IllegalStateException e) {
                sendWSMessage(username, "NOT_IN_LOBBY");
                return;
            }
            sendWSMessage(lobby.getConnectedPlayers(), "USER_LEFT: " + username);
            sendWSMessage(user, "LEFT_LOBBY");
            //TODO: Send WS Message to group and individual
        }
        else if (splitMsg[0].equals("invite user")){
            //TODO
        }
        else if (splitMsg[0].equals("change setting")){
            // TODO
        }
        else if (splitMsg[0].equals("start game")){
            //TODO
        }
        else if (splitMsg[0].equals("submit")){
            // TODO
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

    private void sendWSMessage(List<Account> recipients, String message){
        for(Account user : recipients){
            sendWSMessage(user, message);
        }
    }

    private void sendWSMessage(Account recipient, String message){
        String username = recipient.getUsername();
        sendWSMessage(username, message);
    }

    private void sendWSMessage(String username, String message){
        Session session = usernameSessionMap.get(username);
        sendStringToSession(session, message);
    }

    private void sendStringToSession(Session session, String message){
        try {
            session.getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }
}
