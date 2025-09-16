package com.example.spring_boot.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import com.example.spring_boot.Controller.Question;
import com.example.spring_boot.Model.GameRoom;
import com.example.spring_boot.Model.Player;

import org.springframework.stereotype.Service;

@Service
public class LobbyService {

    private final Map<Long, GameRoom> rooms = new HashMap<>();
    private final AtomicLong roomIdCounter = new AtomicLong(1);

    public GameRoom createRoom(Player player) {
        long roomId = roomIdCounter.getAndIncrement();
        player.setId(System.currentTimeMillis()); // simple unique player id
        player.setHost(true);
        player.setReady(false);

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

        player.setId(System.currentTimeMillis()); // simple unique id
        player.setHost(false);
        player.setReady(false);
        room.getPlayers().add(player);

        return room;
    }

    public GameRoom setPlayerReady(Long roomId, Long playerId, boolean ready) {
        GameRoom room = rooms.get(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        for (Player p : room.getPlayers()) {
            if (p.getId().equals(playerId)) {
                p.setReady(ready);
                break;
            }
        }
        return room;
    }

    public GameRoom startGame(Long roomId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        boolean allReady = room.getPlayers().stream().allMatch(Player::isReady);
        if (!allReady) throw new RuntimeException("Not all players are ready");

        room.setGameState(GameRoom.GameState.IN_PROGRESS);
        return room;
    }

    public List<Question> getQuestions(Long roomId) {
        // For simplicity, return a fixed list of questions.
        return Arrays.asList(
            new Question(1L, "What is 2+2?", Arrays.asList("3","4","5"), 1),
            new Question(2L, "Capital of France?", Arrays.asList("Paris","Rome","Berlin"), 0)
        );
    }
}
