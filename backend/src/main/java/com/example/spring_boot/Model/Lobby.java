package com.example.spring_boot.Model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {
    private String lobbyId;
    private Map<String, Player> players = new ConcurrentHashMap<>(); // sessionId -> Player
    private List<String> chatMessages = new java.util.concurrent.CopyOnWriteArrayList<>();

    public boolean isFull() {
        return players.size() >= 4;
    }
    
    public boolean isEmpty() {
        return players.isEmpty();
    }

    public void setLobbyId(String id) {
        this.lobbyId=id;
    }
    public String getLobbyId(){return this.lobbyId;}

    public List<String> getChatMessages(){return this.chatMessages;}
    public void setChatMessages(List<String> chatMessages){this.chatMessages=chatMessages;}

    public Map<String, Player> getPlayers() {
        return players;
    }


}