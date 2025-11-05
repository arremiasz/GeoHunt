package com.jubair5.geohunt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.net.URI;

public class WebSocketService extends Service {

    private static final String TAG = "WebSocketService";
    private static final String FOREGROUND_CHANNEL_ID = "foreground_channel";
    private WebSocketNotificationClient webSocketClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        startForegroundNotification();
        connectWebSocket();
    }

    private void connectWebSocket() {
        try {
            URI uri = new URI("dummyString");
            webSocketClient = new WebSocketNotificationClient(uri, this);
            webSocketClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "WebSocket connection failed", e);
        }
    }

    private void startForegroundNotification() {
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    FOREGROUND_CHANNEL_ID,
                    "WebSocket Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
                .setContentTitle("Connected to Notifications Server")
                .setContentText("Receiving real-time updates…")
                .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If service is killed, restart it
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don’t use binding
    }
}
