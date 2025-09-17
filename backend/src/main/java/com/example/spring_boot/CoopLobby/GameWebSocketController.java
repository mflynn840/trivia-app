package com.example.spring_boot.CoopLobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.spring_boot.Managers.LobbyManager;
import com.example.spring_boot.Model.ChatMessage;
import com.example.spring_boot.Model.Lobby;
import com.example.spring_boot.Model.Player;

@Controller
public class GameWebSocketController {

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    
    // Create a lobby
    @MessageMapping("/lobby/create")
    @SendTo("/topic/lobby-updates")
    public Lobby createLobby() {
        Lobby lobby = lobbyManager.createLobby();
        return lobby;
    }

    // Join a lobby
    @MessageMapping("/lobby/join/{lobbyId}")
    public void joinLobby(@PathVariable String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.getPlayers().put(player.getSessionId(), player);
            broadcastLobbyUpdate(lobby);
        }
    }

    // Leave a lobby
    @MessageMapping("/lobby/leave/{lobbyId}")
    public void leaveLobby(@PathVariable String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getPlayers().remove(player.getSessionId());
            broadcastLobbyUpdate(lobby);
            if (lobby.isEmpty()) {
                lobbyManager.removeLobby(lobbyId);
            }
        }
    }

    // Toggle ready state
    @MessageMapping("/lobby/ready/{lobbyId}")
    public void toggleReady(@PathVariable String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            Player p = lobby.getPlayers().get(player.getSessionId());
            if (p != null) {
                p.setReady(!p.isReady());
                broadcastLobbyUpdate(lobby);
            }
        }
    }

    // Chat message
    @MessageMapping("/lobby/chat/{lobbyId}")
    public void sendChat(@PathVariable String lobbyId, ChatMessage msg) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getChatMessages().add(msg.getUsername() + ": " + msg.getMessage());
            broadcastLobbyUpdate(lobby);
        }
    }

    private void broadcastLobbyUpdate(Lobby lobby) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getLobbyId(), lobby);
    }
}
