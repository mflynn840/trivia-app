package com.example.spring_boot.Model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lobby {

    private String lobbyId;
    private int maxPlayers = 4;
    private Map<String, Player> players = new ConcurrentHashMap<>(); // sessionId -> Player
    private List<String> chatMessages = new CopyOnWriteArrayList<>();
    private GameState gameState = GameState.WAITING;

    // --- Getters & Setters ---
    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public List<String> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<String> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    // --- Convenience Methods ---
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }
}
