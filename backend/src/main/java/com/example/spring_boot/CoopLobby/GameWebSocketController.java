package com.example.spring_boot.CoopLobby;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class GameWebSocketController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Store active games
    private final Map<String, GameSession> activeGames = new ConcurrentHashMap<>();
    private final AtomicInteger gameIdCounter = new AtomicInteger(1);

    @MessageMapping("/game/join")
    @SendTo("/topic/game/{gameId}")
    public Map<String, Object> joinGame(String gameId, String username) {
        GameSession game = activeGames.computeIfAbsent(gameId, k -> new GameSession());
        
        if (game.addPlayer(username)) {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "player_joined");
            response.put("username", username);
            response.put("players", game.getPlayers());
            response.put("gameState", game.getGameState());
            return response;
        }
        
        return Map.of("type", "error", "message", "Game is full or already started");
    }

    @MessageMapping("/game/leave")
    @SendTo("/topic/game/{gameId}")
    public Map<String, Object> leaveGame(String gameId, String username) {
        GameSession game = activeGames.get(gameId);
        if (game != null) {
            game.removePlayer(username);
            Map<String, Object> response = new HashMap<>();
            response.put("type", "player_left");
            response.put("username", username);
            response.put("players", game.getPlayers());
            response.put("gameState", game.getGameState());
            return response;
        }
        return Map.of("type", "error", "message", "Game not found");
    }

    @MessageMapping("/game/start")
    @SendTo("/topic/game/{gameId}")
    public Map<String, Object> startGame(String gameId) {
        GameSession game = activeGames.get(gameId);
        if (game != null && game.canStart()) {
            game.startGame();
            Question question = questionService.getRandomQuestion("easy", null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "game_started");
            response.put("question", question);
            response.put("currentPlayer", game.getCurrentPlayer());
            response.put("gameState", game.getGameState());
            return response;
        }
        return Map.of("type", "error", "message", "Cannot start game");
    }

    @MessageMapping("/game/answer")
    @SendTo("/topic/game/{gameId}")
    public Map<String, Object> submitAnswer(String gameId, Map<String, Object> answerData) {
        GameSession game = activeGames.get(gameId);
        if (game != null && game.isGameActive()) {
            String username = (String) answerData.get("username");
            String answer = (String) answerData.get("answer");
            Long questionId = Long.valueOf(answerData.get("questionId").toString());
            
            boolean isCorrect = questionService.checkAnswer(questionId, answer);
            game.recordAnswer(username, isCorrect);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "answer_submitted");
            response.put("username", username);
            response.put("correct", isCorrect);
            response.put("scores", game.getScores());
            
            // Move to next player or end round
            if (game.isRoundComplete()) {
                game.nextRound();
                Question nextQuestion = questionService.getRandomQuestion("easy", null);
                response.put("nextQuestion", nextQuestion);
                response.put("currentPlayer", game.getCurrentPlayer());
            }
            
            return response;
        }
        return Map.of("type", "error", "message", "Invalid game state");
    }

    // Helper class for game session management
    private static class GameSession {
        private final List<String> players = new java.util.ArrayList<>();
        private final Map<String, Integer> scores = new HashMap<>();
        private final int maxPlayers = 4;
        private String gameState = "waiting";
        private int currentPlayerIndex = 0;
        private int currentRound = 0;
        private final int maxRounds = 10;

        public boolean addPlayer(String username) {
            if (players.size() < maxPlayers && !players.contains(username) && "waiting".equals(gameState)) {
                players.add(username);
                scores.put(username, 0);
                return true;
            }
            return false;
        }

        public void removePlayer(String username) {
            players.remove(username);
            scores.remove(username);
        }

        public boolean canStart() {
            return players.size() >= 2 && "waiting".equals(gameState);
        }

        public void startGame() {
            gameState = "active";
            currentPlayerIndex = 0;
            currentRound = 1;
        }

        public boolean isGameActive() {
            return "active".equals(gameState);
        }

        public void recordAnswer(String username, boolean isCorrect) {
            if (isCorrect) {
                scores.put(username, scores.get(username) + 1);
            }
        }

        public boolean isRoundComplete() {
            return currentPlayerIndex >= players.size() - 1;
        }

        public void nextRound() {
            currentPlayerIndex = 0;
            currentRound++;
            if (currentRound > maxRounds) {
                gameState = "finished";
            }
        }

        public String getCurrentPlayer() {
            if (players.isEmpty()) return null;
            return players.get(currentPlayerIndex);
        }

        public List<String> getPlayers() {
            return new java.util.ArrayList<>(players);
        }

        public Map<String, Integer> getScores() {
            return new HashMap<>(scores);
        }

        public String getGameState() {
            return gameState;
        }
    }
}
