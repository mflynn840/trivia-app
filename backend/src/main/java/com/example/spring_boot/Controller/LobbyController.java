package com.example.spring_boot.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot.Model.AnswerRequest;
import com.example.spring_boot.Model.GameRoom;
import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Service.LobbyService;

@RestController
@RequestMapping("/api/game")
public class LobbyController {

    private final LobbyService lobbyService; 

    public LobbyController(LobbyService lobbyService){
        this.lobbyService=lobbyService;
    }


    @PostMapping("/create")
    public GameRoom createRoom(@RequestBody Player player) {
        return lobbyService.createRoom(player);
    }

    @PostMapping("/join/{roomId}")
    public GameRoom joinRoom(@PathVariable Long roomId, @RequestBody Player player) {
        return lobbyService.joinRoom(roomId, player);
    }

    @PostMapping("/ready/{roomId}/{playerId}")
    public GameRoom setReady(@PathVariable Long roomId,
                             @PathVariable Long playerId,
                             @RequestParam boolean ready) {
        return lobbyService.setPlayerReady(roomId, playerId, ready);
    }

    @PostMapping("/start/{roomId}")
    public GameRoom startGame(@PathVariable Long roomId) {
        return lobbyService.startGame(roomId);
    }

    @GetMapping("/questions/{roomId}")
    public List<Question> getQuestions(@PathVariable Long roomId) {
        return lobbyService.getQuestions(roomId);
    }

    @PostMapping("/submit/{roomId}/{playerId}")
    public Score submitAnswer(@PathVariable Long roomId,
                              @PathVariable Long playerId,
                              @RequestBody AnswerRequest answer) {
        return lobbyService.submitAnswer(roomId, playerId, answer);
    }
}
