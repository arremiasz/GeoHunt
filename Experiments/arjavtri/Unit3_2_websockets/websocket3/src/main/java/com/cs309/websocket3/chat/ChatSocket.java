package com.cs309.websocket3.chat;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/notify/{username}")
public class ChatSocket {

	private static MessageRepository msgRepo;

	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo;
	}

	private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
	private static Map<String, Session> usernameSessionMap = new Hashtable<>();
	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username)
			throws IOException {

		logger.info("User connected: {}", username);

		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);

		// Send prior notifications (history)
		sendMessageToParticularUser(username, getNotificationHistory());

		// Announce join (optional)
		broadcast("🔔 " + username + " connected to notifications.");
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		String username = sessionUsernameMap.get(session);
		logger.info("Received notification from {}: {}", username, message);

		// Determine notification type
		Message.importance importance = Message.importance.LOW;
		String notificationText = "";

		if (message.startsWith("new_challenge:")) {
			String challengeId = message.split(":")[1];
			importance = Message.importance.HIGH;
			notificationText = "🚨 New Challenge Available! ID: " + challengeId;

		} else if (message.startsWith("near_challenge:")) {
			String challengeId = message.split(":")[1];
			importance = Message.importance.MEDIUM;
			notificationText = "📍 You are near the challenge area (ID: " + challengeId + ")!";

		} else if (message.startsWith("finished_challenge:")) {
			String challengeId = message.split(":")[1];
			importance = Message.importance.LOW;
			notificationText = "✅ Challenge Completed! ID: " + challengeId;

		} else {
			// Unknown command, ignore or send back error
			sendMessageToParticularUser(username, "❌ Unknown notification type: " + message);
			return;
		}

		// Create and save notification message
		Message msg = new Message(username, notificationText);
		msg.setImportance(importance);
		msgRepo.save(msg);

		// Broadcast notification to all users
		broadcast(notificationText);
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		String username = sessionUsernameMap.get(session);
		logger.info("User disconnected: {}", username);

		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);

		broadcast("❌ " + username + " disconnected from notifications.");
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("WebSocket Error: ", throwable);
	}

	private void sendMessageToParticularUser(String username, String message) {
		try {
			Session userSession = usernameSessionMap.get(username);
			if (userSession != null && userSession.isOpen()) {
				userSession.getBasicRemote().sendText(message);
			}
		} catch (IOException e) {
			logger.error("Error sending message to {}: {}", username, e.getMessage());
		}
	}

	private void broadcast(String message) {
		sessionUsernameMap.forEach((session, username) -> {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				logger.error("Broadcast error to {}: {}", username, e.getMessage());
			}
		});
	}

	private String getNotificationHistory() {
		List<Message> messages = msgRepo.findAll();
		StringBuilder sb = new StringBuilder();

		if (messages != null && !messages.isEmpty()) {
			for (Message m : messages) {
				sb.append(formatNotification(m)).append("\n");
			}
		}
		return sb.toString();
	}

	private String formatNotification(Message msg) {
		String icon;
		if(msg.getImportance() == Message.importance.HIGH) {
			icon = "🚨";
		} else if (msg.getImportance() == Message.importance.MEDIUM) {
			icon = "📍";
		} else{
			icon = "✅";
		}
		return icon + " [" + msg.getImportance() + "] " + msg.getContent();
	}
}
