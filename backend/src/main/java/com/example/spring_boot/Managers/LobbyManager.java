package com.example.spring_boot.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring_boot.Model.http.PlayerDTO;
import com.example.spring_boot.Model.user.Player;
import com.example.spring_boot.Model.http.AnswerRequest;
import com.example.spring_boot.Repository.QuestionRepository;
import com.example.spring_boot.Service.QuestionService;
import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Model.coop.GameStatus;
import com.example.spring_boot.Model.coop.Lobby;

@Component
public class LobbyManager {


    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();


    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    /**
     * 
     * Create a new empty lobby with the desired name 
     *  The lobby will contain a gamestate in the WAITING_FOR_PLAYERS state
     * @param name
     * @return
     */
    public Lobby createLobby(String name) {
        
        // Enforce non-null and non-empty names
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Lobby name cannot be null or empty.");
        }

        // Enforce uniqueness of lobby names (case-insensitive)
        boolean nameExists = lobbies.values().stream()
            .anyMatch(lobby -> lobby.getName().equalsIgnoreCase(name));

        if (nameExists) {
            throw new IllegalArgumentException("A lobby with the name '" + name + "' already exists.");
        }

        //Create a new lobby and gamestate
        Lobby lobby = new Lobby();
        lobby.setName(name);
        lobbies.put(name, lobby);
        return lobby;
    }

    public Lobby getLobby(String name) {
        return lobbies.get(name);
    }

    public void removeLobby(String name) {
        lobbies.remove(name);
    }

    public Map<String, Lobby> getAllLobbies() {
        return lobbies;
    }

    public boolean removeUserFromAllLobbies(String username) {
        boolean removedAny = false;
        for (Lobby lobby : getAllLobbies().values()) {
            boolean removed = lobby.getPlayers().values().removeIf(p -> p.getUsername().equals(username));
            if(removed) removedAny=true;
        }

        return removedAny;
    }

    // Toggle ready for a specific user
    public void toggleReady(String lobbyName, String username) {
        Lobby lobby = lobbies.get(lobbyName);
        if (lobby == null) throw new IllegalArgumentException("Lobby does not exist");
        PlayerDTO p = lobby.getPlayers().get(username);
        if (p == null) throw new IllegalArgumentException("User does not exist in that lobby");
        p.setReady(!p.isReady());
    }

    public Lobby safeGetLobby(String lobbyName) throws IllegalArgumentException{
        Lobby lobby = lobbies.get(lobbyName);
        if(lobby == null){
            throw new IllegalArgumentException("lobby doesnt exist");
        }
        return lobby;
    }

    public boolean outOfQuestions(Lobby lobby){;
        return lobby.getGameState().getQuestionIdx() >= 
                lobby.getGameState().getNumQuestions();
    }

    public void scoreResponse(Lobby lobby, AnswerRequest answerRequest) {

        Long questionId = answerRequest.getQuestionId();
        Question answered = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        

        //if the answer is correct +1 points
        if(answered.getCorrectAnswer().equals(answerRequest.getSelectedAnswer())){
            lobby.getGameState().addPoints(answerRequest.getUsername(), 1);

        //-1 points
        }else{
            lobby.getGameState().addPoints(answerRequest.getUsername(), -1);
        }

    }

    public void addUser(String lobbyName, PlayerDTO player) {
        Lobby lobby = this.lobbies.get(lobbyName);
        if (lobby == null || lobby.isFull()) { throw new IllegalArgumentException("Invalid lobby");}
        lobby.addPlayer(player);
    }

    /**Pick questions for the game and set the game status to in progress */
    public void startGame(String lobbyName) {
        Lobby lobby = this.lobbies.get(lobbyName);
        if (lobby == null) { throw new IllegalArgumentException("Invalid lobby");}
        List<Question> questions = questionService.getRandomQuestions(
            lobby.getNumQuestions(), 
            lobby.getCategory(), 
            lobby.getDifficulty());
        
        //setup questions and change flag
        lobby.getGameState().setQuestions(questions);
        lobby.getGameState().setQuestionIdx(0);
        lobby.setGameStatus(GameStatus.IN_PROGRESS);
        

    }

    public void leaveLobby(String lobbyId, Player player) {
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null) {throw new IllegalArgumentException("Lobby invalid");}
        lobby.getPlayers().remove(player.getUsername());

        //if the game is over and the last person leaves
        if(lobby.getPlayers().size() == 0){
            lobbies.remove(lobbyId);
        }
        if(lobby.getPlayers().size() < 2){
            lobby.setGameStatus(GameStatus.WAITING_FOR_PLAYERS);
        }
    }
}

