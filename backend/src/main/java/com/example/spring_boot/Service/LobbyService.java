package com.example.spring_boot.Service;

import java.util.List;

import com.example.spring_boot.Controller.Question;
import com.example.spring_boot.Model.GameRoom;
import com.example.spring_boot.Model.Player;

public class LobbyService {

    public GameRoom createRoom(Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createRoom'");
    }

    public GameRoom joinRoom(Long roomId, Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'joinRoom'");
    }

    public GameRoom startGame(Long roomId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startGame'");
    }

    public GameRoom setPlayerReady(Long roomId, Long playerId, boolean ready) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPlayerReady'");
    }

    public List<Question> getQuestions(Long roomId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getQuestions'");
    }
    
}
