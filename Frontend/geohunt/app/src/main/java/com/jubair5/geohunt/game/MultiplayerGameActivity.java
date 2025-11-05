/**
 * Game activity with multiplayer visual elements
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.game;

import android.os.Bundle;

import com.jubair5.geohunt.R;

import org.java_websocket.client.WebSocketClient;

public class MultiplayerGameActivity extends GameActivity {

    private static final String TAG = "MultiplayerGameActivity";
    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.multiplayer_game_activity);
//        setContentView(R.layout.multiplayer_game_activity);
    }

}
