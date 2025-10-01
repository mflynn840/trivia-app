package com.example.spring_boot.Model.coop;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.spring_boot.Model.http.ChatMessage;
import com.example.spring_boot.Model.http.PlayerDTO;

/**
 * A Trivia Coop game lobby
 */
public class Lobby {

    private String name;
    private int maxPlayers = 4;
    private Map<String, PlayerDTO> players = new ConcurrentHashMap<>(); // sessionId -> Player
    private List<ChatMessage> chatMessages = new CopyOnWriteArrayList<>();
    private GameState gameState = new GameState();

    // --- Constructors ----
    public Lobby() { this.name = Integer.toString((new Random(1).nextInt())); }
    public Lobby(String name) { this.name = name; }

    // --- Getters/Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public Map<String, PlayerDTO> getPlayers() { return players; }
    
    public void addPlayer(PlayerDTO p){
        this.players.put(p.getUsername(), p);
        this.gameState.addUser(p.getUsername());
    }

    public void setPlayers(Map<String, PlayerDTO> players) { 
        this.players = players; 
    }


    public List<ChatMessage> getChatMessages() { return chatMessages; }
    public void setChatMessages(List<ChatMessage> chatMessages) { this.chatMessages = chatMessages; }

    public GameStatus getGameStatus() { return this.gameState.getGameStatus(); }
    public void setGameStatus(GameStatus gameStatus) { this.gameState.setGameStatus(gameStatus); }

    public GameState getGameState(){return this.gameState;}
    public void setGameState(GameState gameState){this.gameState = gameState;}


    // --- Convenience Methods ---
    public boolean isFull() { return players.size() >= maxPlayers; }
    public boolean isEmpty() { return players.isEmpty(); }
    public boolean isReady() { return this.getPlayers().values().stream().allMatch(PlayerDTO::isReady); }
    public void advanceQuestion(){gameState.setQuestionIdx(gameState.getQuestionIdx()+1);}
    
}

