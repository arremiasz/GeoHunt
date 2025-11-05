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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;


@ServerEndpoint("/notify/{name}")
@Component
public class Notifications {
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private static Map<String, java.util.Set<String>> groupMembersMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(Notifications.class);
    private String currentGroup;

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
        //Multiplayer Invite: Invite <username> <groupId> //GroupId "N" if none currently. Will return a groupId.
        //Multiplayer Invite Accept: AcceptInvite <groupId>
        //Multiplayer leave group: Leave <groupId>
        //Multplayer Challenge Complete: MultComplete <groupId>
        //Solo challenge complete: ChallComplete
        //Custom Group Message: CustomGroupMsg <groupId> msg
        //custom DM: CustomDM <username: Note: username is self name if you want to send notif to yourself.> msg
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

            if (groupId.equals("N")) {
                String newId = username + "sGrp";
                groupMembersMap.put(newId, new HashSet<>());
                groupMembersMap.get(newId).add(username);
                sendMessageToParticularUser(username, "Your new groupId is: " + newId);
                currentGroup = newId;
                groupId = newId; // update for next steps
            }

            String msg = username + " invited you to join their group (" + groupId + ")";
            sendMessageToParticularUser(targetUser, msg);
        }

        else if (message.startsWith("AcceptInvite")) {
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 2) return;

            String groupId = splitMsg[1];
            if (!groupMembersMap.containsKey(groupId)) {
                sendMessageToParticularUser(username, "Group does not exist.");
                return;
            }

            boolean alreadyIn = !groupMembersMap.get(groupId).add(username);
            if (alreadyIn) {
                sendMessageToParticularUser(username, "You are already in this group.");
                return;
            }

            currentGroup = groupId;
            sendMessageToParticularUser(username, "You have been added to the group!");
            broadcastInGroup(username + " has joined your group!", groupId, username);
        } else if(message.startsWith("Leave")){
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 2) return;
            String groupId = splitMsg[1];
            String msg = username + " left the group!";
            if (!groupMembersMap.containsKey(groupId)) {
                sendMessageToParticularUser(username, "Group does not exist.");
                return;
            }
            boolean didRemove = groupMembersMap.get(groupId).remove(username);
            if (groupMembersMap.get(groupId).isEmpty()) {
                groupMembersMap.remove(groupId);
            }
            if (didRemove) {
                broadcastInGroup(msg, groupId, username);
            } else {
                sendMessageToParticularUser(username, "That group does not exist or you are not in that group.");
            }

        } else if(message.startsWith("MultComplete")){
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 2) return;
            String groupId = splitMsg[1];
            if (!groupMembersMap.containsKey(groupId)) {
                sendMessageToParticularUser(username, "Group does not exist.");
                return;
            }

            String msg = username + " has finished the challenge!";
            broadcastInGroup(msg, groupId, username);
        } else if(message.startsWith("ChallComplete")){
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 1) return;
            sendMessageToParticularUser(username, "You have completed the challenge!");
        } else if (message.startsWith("CustomDM")) {
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 2) return;
            String targetUser = splitMsg[1];
            int startIndex = 2;
            int endIndex = splitMsg.length - 1;
            String msg = String.join(" ", Arrays.copyOfRange(splitMsg, startIndex, splitMsg.length));
            sendMessageToParticularUser(targetUser, msg);
        } else if (message.startsWith("CustomGroupMsg")) {
            String[] splitMsg = message.split(" ");
            if (splitMsg.length < 2) return;
            String groupId = splitMsg[1];
            int startIndex = 2;
            int endIndex = splitMsg.length - 1;
            String msg = String.join(" ", Arrays.copyOfRange(splitMsg, startIndex, splitMsg.length));
            broadcastInGroup(msg, groupId, username);
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

        // send the message to the group

        if(currentGroup != null) {
            broadcastInGroup(username + " has disconnected.", currentGroup, username);
            if (!groupMembersMap.containsKey(currentGroup)) {
                sendMessageToParticularUser(username, "Group does not exist.");
                return;
            }
            groupMembersMap.get(currentGroup).remove(username);
            if(groupMembersMap.get(currentGroup).size() == 0) {
                groupMembersMap.remove(currentGroup);
            }
        }
    }



    private boolean broadcastInGroup(String message, String groupId, String avoid) {
        if (!groupMembersMap.containsKey(groupId)) return false;

        for (String a : groupMembersMap.get(groupId)) {
            if (!a.equals(avoid)) {
                Session userSession = usernameSessionMap.get(a);
                if (userSession != null) {
                    try {
                        userSession.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        logger.info("[Broadcast Exception] " + e.getMessage());
                    }
                }
            }
        }
        return true;
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
