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

    public Lobby createLobby(String name) {
        
        // Enforce non-null and non-empty names
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lobby name cannot be null or empty.");
        }

        // Enforce uniqueness of lobby names (case-insensitive)
        boolean nameExists = lobbies.values().stream()
            .anyMatch(lobby -> lobby.getName().equalsIgnoreCase(name));

        if (nameExists) {
            throw new IllegalArgumentException("A lobby with the name '" + name + "' already exists.");
        }

        String id = "lobby-" + lobbyCounter.getAndIncrement();
        Lobby lobby = new Lobby();
        lobby.setName(id);
        lobby.setName(name);
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
