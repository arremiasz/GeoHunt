package com.jubair5.geohunt;

import android.content.Context;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class WebSocketNotificationClient extends WebSocketClient {

    private static final String TAG = "WebSocketNotification";
    private final Context context;

    public WebSocketNotificationClient(URI serverUri, Context context){
        super(serverUri);
        this.context = context;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "Notifications Connected to WebSocker Server");

    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG,"Message Received: " + message);

        try{
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");

            switch (type){
                case "friendRequest":
                    friendRequestNotification(json);
                    break;
                default:
                    Log.w(TAG, "Unknown message type: " + type);

            }

        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void friendRequestNotification(JSONObject json) throws JSONException {
        String from = json.getString("from");
        NotificationUtils.showNotification(
                context,
                "New Friend Request",
                "From: " + from,
                "friend_request_channel"
        );
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "Connection closed: " + reason);

    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "WebSocket Error: " + ex);

    }
}
