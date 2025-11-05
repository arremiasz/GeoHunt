package com.geohunt.backend.WebSockets;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@ServerEndpoint("/notify/{name}")
@Component
public class Notifications {
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private static Map<String, java.util.Set<String>> groupMembersMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(Notifications.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name) throws IOException {
        logger.info("[onOpen]: {}", name);

        if (usernameSessionMap.containsKey(name)) {
            session.getBasicRemote().sendText("Username already exists");
            session.close();
        } else {
            // map current session with username
            sessionUsernameMap.put(session, name);

            // map current username with session
            usernameSessionMap.put(name, session);

        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {

        // get the username by session
        String username = sessionUsernameMap.get(session);

        // server side log
        logger.info("[onMessage] " + username + ": " + message);

        //Friend Request: FriendReq <username>
        //Friend Request Accepted: FriendAcc <username>
        //Multiplayer Invite: Invite <username> <groupId>
        if (message.startsWith("FriendReq")) {
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 2) return;

            String targetUser = splitMsg[1];
            String msg = "You have been sent a friend request by: " + username;
            sendMessageToParticularUser(targetUser, msg);
        } else if (message.startsWith("FriendAcc")) {
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 2) return;

            String targetUser = splitMsg[1];
            String msg = username + " has accepted your friend request!";
            sendMessageToParticularUser(targetUser, msg);
        } else if (message.startsWith("Invite")) {
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 3) return;

            String targetUser = splitMsg[1];
            String groupId = splitMsg[2];
            String msg = username + " invited you to join their group!";
            broadcastInGroup(msg, groupId, targetUser);
        } else if(message.startsWith("InviteAccepted")) {
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 3) return;

            String targetUser = splitMsg[1];
            String groupId = splitMsg[2];
            String msg = username + " accepted your group invite!"; //ToFinish
        }

    }

    @OnClose
    public void onClose(Session session) throws IOException {

        // get the username from session-username mapping
        String username = sessionUsernameMap.get(session);

        // server side log
        logger.info("[onClose] " + username);

        // remove user from memory mappings
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        // send the message to chat
        broadcast(username + " disconnected");
    }

    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }

    private boolean broadcastInGroup(String message, String groupId, String avoid) {
        if( groupMembersMap.get(groupId) != null ) {
            for(String a: groupMembersMap.get(groupId)) {
                if(!a.equals(avoid)) {
                    Session userSession = usernameSessionMap.get(a);
                    if (userSession != null) {
                        try{
                            userSession.getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            logger.info("[Broadcast Exception] " + e.getMessage());
                        }
                    }
                }
                return true;
            }
        } else {
            return false;
        }

    }


    private void sendMessageToParticularUser(String username, String message) {
        try {
            Session userSession = usernameSessionMap.get(username);
            if (userSession != null && userSession.isOpen()) {
                userSession.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }
}
