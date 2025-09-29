package com.example.spring_boot.Model;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lobby {

    private String name;
    private int maxPlayers = 4;
    private Map<String, PlayerDTO> players = new ConcurrentHashMap<>(); // sessionId -> Player
    private List<ChatMessage> chatMessages = new CopyOnWriteArrayList<>();
    private GameStatus lobbyStatus = GameStatus.WAITING;
    private GameState gameState;
    
    public Lobby() {
        // default constructor for Spring/Gson/STOMP deserialization
        this.name = Integer.toString((new Random(1).nextInt()));
    }
    public Lobby(String name){
        this.name=name;
    }
    // --- Getters & Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Map<String, PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, PlayerDTO> players) {
        this.players = players;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public GameStatus getLobbyStatus() {
        return this.lobbyStatus;
    }

    public void setLobbyStatus(GameState gameState) {
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
