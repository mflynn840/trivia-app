package com.example.spring_boot.Managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

import com.example.spring_boot.Model.GameState;
import com.example.spring_boot.Model.GameStatus;
import com.example.spring_boot.Model.Lobby;
import com.example.spring_boot.Model.PlayerDTO;

@Component
public class LobbyManager {
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    /**
     * 
     * Create a new empty lobby with the desired name 
     *  The lobby will contain a gamestate in the WAITING_FOR_PLAYERS state
     * @param name
     * @return
     */
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

        //Create a new lobby and gamestate
        Lobby lobby = new Lobby();
        GameState gameState = new GameState();
        gameState.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        lobby.setName(name);
        lobby.setGameState(gameState);
        lobbies.put(name, lobby);
        return lobby;
    }

    public Lobby getLobby(String name) {
        return lobbies.get(name);
    }

    public void removeLobby(String name) {
        lobbies.remove(name);
    }

    public Map<String, Lobby> getAllLobbies() {
        return lobbies;
    }

    public boolean removeUserFromAllLobbies(String username) {
        boolean removedAny = false;
        for (Lobby lobby : getAllLobbies().values()) {
            boolean removed = lobby.getPlayers().values().removeIf(p -> p.getUsername().equals(username));
            if(removed) removedAny=true;
        }

        return removedAny;
    }

    // Toggle ready for a specific user
    public void toggleReady(String lobbyName, String username) {
        Lobby lobby = lobbies.get(lobbyName);
        if (lobby == null) throw new IllegalArgumentException("Lobby does not exist");
        PlayerDTO p = lobby.getPlayers().get(username);
        if (p == null) throw new IllegalArgumentException("User does not exist in that lobby");
        p.setReady(!p.isReady());
    }
}

