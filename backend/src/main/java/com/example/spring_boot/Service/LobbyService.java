package com.example.spring_boot.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Model.Score;
import com.example.spring_boot.Model.AnswerRequest;
import com.example.spring_boot.Model.GameRoom;
import com.example.spring_boot.Model.Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LobbyService {

    private final Map<Long, GameRoom> rooms = new HashMap<>();
    private final AtomicLong roomIdCounter = new AtomicLong(1);
    private final QuestionService questionService;


    public LobbyService(QuestionService questionService){
        this.questionService = questionService;
    }

    public GameRoom createRoom(Player player) {
        long roomId = roomIdCounter.getAndIncrement();
        player.setIsHost(true);
        player.setIsReady(false);

        GameRoom room = new GameRoom();
        room.setId(roomId);
        room.setPlayers(new ArrayList<>());
        room.getPlayers().add(player);
        room.setGameState(GameRoom.GameState.WAITING_FOR_PLAYERS);

        rooms.put(roomId, room);
        return room;
    }

    public GameRoom joinRoom(Long roomId, Player player) {
        GameRoom room = rooms.get(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        player.setIsHost(false);
        player.setIsReady(false);
        room.getPlayers().add(player);

        return room;
    }

    public GameRoom setPlayerReady(Long roomId, Long playerId, boolean ready) {
        GameRoom room = rooms.get(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        for (Player p : room.getPlayers()) {
            if (p.getId().equals(playerId)) {
                p.setIsReady(ready);
                break;
            }
        }
        return room;
    }

    public GameRoom startGame(Long roomId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        boolean allReady = room.getPlayers().stream().allMatch(Player::getIsReady);
        if (!allReady) throw new RuntimeException("Not all players are ready");

        room.setGameState(GameRoom.GameState.IN_PROGRESS);
        return room;
    }

    public List<Question> getQuestions(Long roomId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        // Example: fetch 5 random questions per game
        return questionService.getRandomQuestions(5);
    }

    public Score submitAnswer(Long roomId, Long playerId, AnswerRequest answer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'submitAnswer'");
    }
}
