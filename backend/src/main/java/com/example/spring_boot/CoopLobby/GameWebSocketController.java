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

    @MessageMapping("/lobby/getAll")
    public void sendAllLobbies() {
        Map<String, Lobby> all = lobbyManager.getAllLobbies();
        messagingTemplate.convertAndSend("/topic/lobby/all", all);  // Send all lobbies to the client
    }

    @MessageMapping("/lobby/create")
    public void createLobby(@Payload CreateLobbyRequest request) {
        try {
            Lobby lobby = lobbyManager.createLobby(request.getName());
            sendAllLobbies();  // Notify all clients about the updated list of lobbies
        } catch (IllegalArgumentException e) {
            messagingTemplate.convertAndSend("/topic/lobby/errors", Map.of(
                "error", true,
                "message", e.getMessage()
            ));
        }
    }

    @MessageMapping("/lobby/join/{lobbyId}")
    public void joinLobby(String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.getPlayers().put(player.getSessionId(), player);
            broadcastLobbyState(lobby);  // Update clients with the new lobby state
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
                sendAllLobbies();  // If the lobby is empty, send updated lobby list
            }
        }
    }

    @MessageMapping("/lobby/ready/{lobbyId}")
    public void toggleReady(String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            Player p = lobby.getPlayers().get(player.getSessionId());
            if (p != null) {
                p.setReady(!p.isReady());  // Toggle ready status
                broadcastLobbyState(lobby);
            }
        }
    }

    @MessageMapping("/lobby/chat/{lobbyId}")
    public void sendChat(String lobbyId, ChatMessage msg) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getChatMessages().add(msg);
            broadcastLobbyState(lobby);  // Broadcast the new chat message
        }
    }

    private void broadcastLobbyState(Lobby lobby) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName(), lobby);  // Broadcast specific lobby state
    }
}
