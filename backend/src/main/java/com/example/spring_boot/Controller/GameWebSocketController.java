package com.example.spring_boot.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.example.spring_boot.Managers.LobbyManager;
import com.example.spring_boot.Model.coop.GameStatus;
import com.example.spring_boot.Model.coop.Lobby;
import com.example.spring_boot.Model.http.AnswerRequest;
import com.example.spring_boot.Model.http.ChatMessage;
import com.example.spring_boot.Model.http.CreateLobbyRequest;
import com.example.spring_boot.Model.http.PlayerDTO;
import com.example.spring_boot.Model.http.TimerRequest;
import com.example.spring_boot.Model.user.Player;

import java.util.Map;


/**
 * 
 * Implements a STOMP endpoint for Coop gameplay
 */
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
    @MessageMapping("/lobby/join/{lobbyName}")
    public void joinLobby(@DestinationVariable String lobbyName, @Payload PlayerDTO player) {
        System.out.println("request to join server");
        try{
            lobbyManager.addUser(lobbyName, player);
            Lobby lobby = lobbyManager.getLobby(lobbyName);
            broadcastLobbyState(lobby); //update for other users

            if(lobby.getPlayers().size() >= 2){
                lobby.setGameStatus(GameStatus.WAITING_FOR_READY);
            }
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
        }
    }
    /**
     * Leave this current lobby
     * @param lobbyId
     * @param player
     */
    @MessageMapping("/lobby/leave/{lobbyId}")
    public void leaveLobby(String lobbyId, @Payload Player player) {
        try{
            lobbyManager.leaveLobby(lobbyId, player);
            broadcastLobbyState(lobbyManager.getLobby(lobbyId));
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
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
            Lobby currentLobby = lobbyManager.getLobby(lobbyName);
            //if the whole lobby is ready start the game
            if(currentLobby.getGameStatus().equals(GameStatus.WAITING_FOR_READY)
                && currentLobby.isReady() 
            ){
                startGame(currentLobby.getName());
            }
            broadcastLobbyState(currentLobby);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }
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

    //to start a timer, send the start time, and duration to the frontend
    public void startTimer60(Long questionId){
        TimerRequest t = new TimerRequest();
        t.setStartEpochTime(System.currentTimeMillis());
        t.setDurationMs(Long.valueOf(60000));
        t.setQuestionId(questionId);
        
        
    }

    /**
     * atomically submit the answer and advance to the next question
     *  -broadcast new gamestate to all members
     */
    @MessageMapping("/lobby/submit/{lobbyId}")
    public void submitAnswer(@DestinationVariable String lobbyId, 
                            @Payload AnswerRequest answerRequest){

        System.out.println("answerRequest: " + answerRequest.toString());
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        if (lobby != null) {
            //1. lock the resource so nobody else can answer
                //TODO i dont know if i need to do this
            //2. assign points for the submitted answer
            lobbyManager.scoreResponse(lobby, answerRequest);

            //4. advance question for the lobby
            lobby.advanceQuestion();

            //3. check if this was the last question
            if(lobbyManager.outOfQuestions(lobby)){
                System.out.println("Last question, game over");
                lobby.setGameStatus(GameStatus.FINISHED);
            }

            //5.broadcast the new lobby to all users
            broadcastLobbyState(lobby); 
        }
    }

    //Private helper methods
    private void startGame(String lobby){
        lobbyManager.startGame(lobby);
    }

    // Lobby state updates helper
    private void broadcastLobbyState(Lobby lobby) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName() + "/state", lobby);
    }

    // Chat message updates helper
    private void broadcastChatMessage(Lobby lobby, ChatMessage msg) {
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getName() + "/chat", msg);
    }


}
