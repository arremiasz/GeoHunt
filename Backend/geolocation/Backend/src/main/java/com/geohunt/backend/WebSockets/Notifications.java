package com.geohunt.backend.WebSockets;

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

    private final Logger logger = LoggerFactory.getLogger(Notifications.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("name") String name) throws IOException {
        logger.info("[onOpen]: " + name);

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



    }
}
