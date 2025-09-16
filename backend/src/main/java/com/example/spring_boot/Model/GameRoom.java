package com.example.spring_boot.Model;

import java.util.List;

public class GameRoom {
    private Long id;
    private List<Player> players;
    private GameState gameState;

    public enum GameState { 
        WAITING_FOR_PLAYERS, 
        IN_PROGRESS, 
        FINISHED 
    }

    // Constructors
    public GameRoom() {}

    public GameRoom(Long id, List<Player> players, GameState gameState) {
        this.id = id;
        this.players = players;
        this.gameState = gameState;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
