package com.example.spring_boot.CoopLobby;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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

    @MessageMapping("/lobby/create")
    @SendTo("/topic/lobby-updates")
    public Lobby createLobby(@Payload CreateLobbyRequest request) {
        Lobby lobby = lobbyManager.createLobby(request.getName());
        System.out.println("Created lobby: " + lobby.getName());
        return lobby;
    }
    

    @MessageMapping("/lobby/join/{lobbyId}")
    public void joinLobby(String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.getPlayers().put(player.getSessionId(), player);
            broadcastLobbyUpdate(lobby);
        }
    }

    @MessageMapping("/lobby/leave/{lobbyId}")
    public void leaveLobby(String lobbyId, Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getPlayers().remove(player.getSessionId());
            broadcastLobbyUpdate(lobby);
            if (lobby.isEmpty()) {
                lobbyManager.removeLobby(lobbyId);
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
                broadcastLobbyUpdate(lobby);
            }
        }
    }

    @MessageMapping("/lobby/chat/{lobbyId}")
    public void sendChat(String lobbyId, ChatMessage msg) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getChatMessages().add(msg.getUsername() + ": " + msg.getMessage());
            broadcastLobbyUpdate(lobby);
        }
    }

    private void broadcastLobbyUpdate(Lobby lobby) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName(), lobby);
    }

    @MessageMapping("/lobby/ping")
    @SendTo("/topic/lobby/ping")
    public String ping() {
        return "pong";
    }


    @MessageMapping("/lobby/getAll")
    @SendTo("/topic/lobby/all")
    public Map<String, Lobby> getAllLobbies() {
        return lobbyManager.getAllLobbies();
    }

    
}
