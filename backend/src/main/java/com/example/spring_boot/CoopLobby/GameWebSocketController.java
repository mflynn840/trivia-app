package com.example.spring_boot.CoopLobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
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


    /**
     * Send an initial copy of the lobby to new users
     * @param lobbyName
     * @return
     */ 
    @SubscribeMapping("/lobby/{lobbyName}/state")
    public Lobby sendInitialLobbyState(@DestinationVariable String lobbyName) {
        System.out.println("Sending initial lobbystate");
        Lobby lobby = lobbyManager.getLobby(lobbyName);
        return lobby; 
    }

    /**
     * Send a list of all lobbies to requester
     */
    @MessageMapping("/lobby/getAll")
    public void sendAllLobbies() {
        Map<String, Lobby> all = lobbyManager.getAllLobbies();
        messagingTemplate.convertAndSend("/topic/lobby/all", all);  // Send all lobbies to the client
    }

    @MessageMapping("/lobby/get/{name}")
    public void sendLobby(@DestinationVariable String lobbyId){
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobby);
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
    public void joinLobby(@DestinationVariable String lobbyId, @Payload Player player) {
        System.out.println("request to join server");
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.getPlayers().put(player.getSessionId(), player);
            broadcastLobbyState(lobby);  // Update clients with the new lobby state
        }
    }

    @MessageMapping("/lobby/leave/{lobbyId}")
    public void leaveLobby(String lobbyId, @Payload Player player) {
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

    // Lobby state updates helper
    private void broadcastLobbyState(Lobby lobby) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName() + "/state", lobby);
    }

    // Chat message updates helper
    private void broadcastChatMessage(Lobby lobby, ChatMessage msg) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName() + "/chat", msg);
    }

    /**
     * Send a chat message to the requested lobby
     * -broadcast the new message to all other users
     * 
     * @param lobbyId
     * @param msg
     */
    @MessageMapping("/lobby/chat/{lobbyId}")
    public void sendChat(@DestinationVariable String lobbyId, @Payload ChatMessage msg) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getChatMessages().add(msg);
            broadcastChatMessage(lobby, msg);  // Only send the new chat message
        }
    }




}
