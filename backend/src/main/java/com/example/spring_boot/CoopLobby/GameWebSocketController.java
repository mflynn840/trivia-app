package com.example.spring_boot.CoopLobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.spring_boot.Managers.LobbyManager;
import com.example.spring_boot.Model.ChatMessage;
import com.example.spring_boot.Model.GameState;
import com.example.spring_boot.Model.Lobby;
import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Model.PlayerDTO;
import com.example.spring_boot.dto.CreateLobbyRequest;

import java.util.Map;

@Controller
public class GameWebSocketController {

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    /**
     * Send a list of all lobbies to requester
     */
    @MessageMapping("/lobby/getAll")
    public void sendAllLobbies() {
        Map<String, Lobby> all = lobbyManager.getAllLobbies();
        messagingTemplate.convertAndSend("/topic/lobby/all", all);  // Send all lobbies to the client
    }

    /**
     * Broadcast a copy of the requested lobby object
     * @param lobbyId
     */
    @MessageMapping("/lobby/get/{lobbyId}")
    public void sendLobby(@DestinationVariable String lobbyId){
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, lobby);
    }

    /**
     * Create a new lobby using DTO
     * @param request
     */
    @MessageMapping("/lobby/create")
    public void createLobby(@Payload CreateLobbyRequest request) {
        try {
            lobbyManager.createLobby(request.getName());
            sendAllLobbies();  // Notify all clients about the updated list of lobbies
        } catch (IllegalArgumentException e) {
            messagingTemplate.convertAndSend("/topic/lobby/errors", Map.of(
                "error", true,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Add a user to the lobby object
     * Notify all other users that they joined
     * send the player who just joined a coppy of the old lobby state
     * @param lobbyId
     * @param player
     */
    @MessageMapping("/lobby/join/{lobbyId}")
    public void joinLobby(@DestinationVariable String lobbyId, @Payload PlayerDTO player) {
        System.out.println("request to join server");
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null && !lobby.isFull()) {
            lobby.getPlayers().put(player.getUsername(), player);
            broadcastLobbyState(lobby);  // Update all lobby users with the new lobby state
        }else{
            System.out.println("Failed to join lobby: lobby is full of null");
        }
    }

    /**
     * Leave this current lobby
     * @param lobbyId
     * @param player
     */
    @MessageMapping("/lobby/leave/{lobbyId}")
    public void leaveLobby(String lobbyId, @Payload Player player) {
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getPlayers().remove(player.getUsername());
            broadcastLobbyState(lobby);
            if (lobby.isEmpty()) {
                lobbyManager.removeLobby(lobbyId);
                sendAllLobbies();  // If the lobby is empty, send updated lobby list
            }
        }
    }

    /**
     * Set the given players status to ready in this given lobby
     * @param lobbyId
     * @param player
     */
    @MessageMapping("/lobby/ready/{lobbyName}")
    public void toggleReady(@DestinationVariable("lobbyName") String lobbyName, Map<String, String> payload) {        
        String username = payload.get("username");

        try{
            lobbyManager.toggleReady(lobbyName, username);
            broadcastLobbyState(lobbyManager.getLobby(lobbyName));
        }catch(IllegalArgumentException e){
            System.out.println(e.getStackTrace());
        }
    }

    @MessageMapping("/lobby/submit/{lobbyId}")
    public void submitAnswer(){

    }

    // Lobby state updates helper
    private void broadcastLobbyState(Lobby lobby) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName() + "/state", lobby);
    }

    // Chat message updates helper
    private void broadcastChatMessage(Lobby lobby, ChatMessage msg) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName() + "/chat", msg);
    }

    // Game state updates helper
    private void broadcastGameState(Lobby lobby, GameState g){
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName() + "/chat", g);
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
        System.out.println("Recieved message: " + msg.getMessage());
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            lobby.getChatMessages().add(msg);
            broadcastChatMessage(lobby, msg);  // Only send the new chat message
        }
    }

    /**
 * Removes a user from all lobbies and broadcasts the updated states
 * @param username the username of the user to remove
 */
public void removeUserFromAllLobbies(String username) {
    boolean removedAny = false;
    for (Lobby lobby : lobbyManager.getAllLobbies().values()) {
        boolean removed = lobby.getPlayers().values().removeIf(p -> p.getUsername().equals(username));
        if (removed) {
            removedAny = true;
            broadcastLobbyState(lobby); // update all clients in this lobby
        }
    }
    if (removedAny) {
        sendAllLobbies(); // also broadcast the updated list of lobbies
    }
}




}
