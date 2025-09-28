package com.example.spring_boot.CoopLobby;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot.Managers.LobbyManager;
import com.example.spring_boot.Model.Lobby;
import com.example.spring_boot.dto.CreateLobbyRequest;

@RestController
@RequestMapping("/api/lobbies")
public class LobbyInfoController {

    @Autowired
    private LobbyManager lobbyManager;

    /**
     * Returns a list of all available lobby names.
     * This endpoint is used by the frontend to display the list of servers
     * without subscribing to each lobby’s state via WebSocket.
     */
    @GetMapping
    public ResponseEntity<List<String>> getAllLobbies() {
        return ResponseEntity.ok(lobbyManager.getAllLobbies().values().stream()
                .map(Lobby::getName)
                .toList());
    }

    /**
     * Creates a new lobby with the given name.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createLobby(@RequestBody CreateLobbyRequest request) {
        try {
            lobbyManager.createLobby(request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body("Lobby created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}