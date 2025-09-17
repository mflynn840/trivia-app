package com.example.spring_boot.Managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.example.spring_boot.Model.Lobby;

@Component
public class LobbyManager {
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private final AtomicInteger lobbyCounter = new AtomicInteger(1);

    public Lobby createLobby() {
        String id = "lobby-" + lobbyCounter.getAndIncrement();
        Lobby lobby = new Lobby();
        lobby.setLobbyId(id);
        lobbies.put(id, lobby);
        return lobby;
    }

    public Lobby getLobby(String id) {
        return lobbies.get(id);
    }

    public void removeLobby(String id) {
        lobbies.remove(id);
    }

    public Map<String, Lobby> getAllLobbies() {
        return lobbies;
    }
}
