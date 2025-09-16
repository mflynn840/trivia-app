package com.example.spring_boot.Model;

import java.util.List;

public class GameRoom {
    private Long id;
    private List<Player> players;
    private GameState gameState;

    public enum GameState { WAITING_FOR_PLAYERS, IN_PROGRESS, FINISHED }
}
