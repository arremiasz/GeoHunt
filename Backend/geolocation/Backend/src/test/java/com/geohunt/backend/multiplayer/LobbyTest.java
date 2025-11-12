package com.geohunt.backend.multiplayer;

import com.geohunt.backend.database.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    @Test
    void addPlayer() {
        Account a1 = new Account("test1", "password");
        Account a2 = new Account("test2", "password");
        Lobby lobby = new Lobby(a1);
        lobby.addPlayer(a1);

        assertTrue(lobby.hasUser(a1));
        assertFalse(lobby.hasUser(a2));
    }

    @Test
    void removePlayer() {

        Account a1 = new Account("test1", "password");
        Account a2 = new Account("test2", "password");
        Lobby lobby = new Lobby(a1);
        lobby.addPlayer(a1);
        lobby.removePlayer(a1);
        lobby.addPlayer(a2);

        assertFalse(lobby.hasUser(a1));
        assertTrue(lobby.hasUser(a2));
    }

    @Test
    void setLobbyLeader() {
        Account a1 = new Account("test1", "password");
        Account a2 = new Account("test2", "password");
        Lobby lobby = new Lobby(a1);
        lobby.addPlayer(a1);
        lobby.addPlayer(a2);
        lobby.setLobbyLeader(a1);

        assertTrue(lobby.isLobbyLeader(a1));
        assertFalse(lobby.isLobbyLeader(a2));
    }
}