package com.example.spring_boot.CoopLobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.spring_boot.Managers.LobbyManager;
import com.example.spring_boot.Model.ChatMessage;
import com.example.spring_boot.Model.Lobby;
import com.example.spring_boot.Model.Player;
import com.example.spring_boot.dto.CreateLobbyRequest;

import java.util.Map;

@Controller
public class GameWebSocketController {

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // -------------------
    // All Lobbies Topic
    // -------------------
    @MessageMapping("/lobby/getAll")
    public void sendAllLobbies() {
        Map<String, Lobby> all = lobbyManager.getAllLobbies();
        messagingTemplate.convertAndSend("/topic/lobby/all", all);
    }

    // -------------------
    // Create Lobby
    // -------------------
    @MessageMapping("/lobby/create")
    public void createLobby(@Payload CreateLobbyRequest request) {
        try {
            Lobby lobby = lobbyManager.createLobby(request.getName());
            System.out.println("Created lobby: " + lobby.getName());

            // Notify all subscribers about updated list of lobbies
            sendAllLobbies();

        } catch (IllegalArgumentException e) {
            messagingTemplate.convertAndSend("/topic/lobby/errors", Map.of(
                "error", true,
                "message", e.getMessage()
            ));
        }
    }

    // -------------------
    // Lobby-specific actions
    // -------------------
    @MessageMapping("/lobby/join/{lobbyId}")
    public void joinLobby(String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.getPlayers().put(player.getSessionId(), player);
            broadcastLobbyState(lobby);
        }
    }

    @MessageMapping("/lobby/leave/{lobbyId}")
    public void leaveLobby(String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getPlayers().remove(player.getSessionId());
            broadcastLobbyState(lobby);
            if (lobby.isEmpty()) {
                lobbyManager.removeLobby(lobbyId);
                sendAllLobbies(); // update all lobbies topic
            }
        }
    }

    @MessageMapping("/lobby/ready/{lobbyId}")
    public void toggleReady(String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            Player p = lobby.getPlayers().get(player.getSessionId());
            if (p != null) {
                p.setReady(!p.isReady());
                broadcastLobbyState(lobby);
            }
        }
    }

    @MessageMapping("/lobby/chat/{lobbyId}")
    public void sendChat(String lobbyId, ChatMessage msg) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getChatMessages().add(msg.getUsername() + ": " + msg.getMessage());
            broadcastLobbyState(lobby);
        }
    }


    
    // -------------------
    // Helper: Broadcast lobby state to lobby-specific topic
    // -------------------
    private void broadcastLobbyState(Lobby lobby) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName(), lobby);
    }



    // Optional: ping for connection testing
    @MessageMapping("/lobby/ping")
    public void ping() {
        messagingTemplate.convertAndSend("/topic/lobby/ping", "pong");
    }
}
