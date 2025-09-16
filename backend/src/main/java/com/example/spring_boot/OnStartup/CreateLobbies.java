package com.example.spring_boot.OnStartup;

import com.example.spring_boot.Model.GameRoom;
import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Service.LobbyService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreateLobbies {

    @Autowired
    private LobbyService lobbyService;

    // Keep references to the 4 lobbies
    private final List<GameRoom> lobbies = new ArrayList<>(4);

    
    public void initLobbies() {
        // Initialize 4 lobbies if they don't exist
        for (int i = 0; i < 4; i++) {
            if (lobbies.size() <= i || lobbies.get(i) == null) {
                Player dummyHost = new Player();
                dummyHost.setId(-1L); // dummy host id
                dummyHost.setUsername("Host_" + (i + 1));
                dummyHost.setIsHost(true);
                dummyHost.setIsReady(false);

                GameRoom lobby = lobbyService.createRoom(dummyHost);
                if (lobbies.size() > i) {
                    lobbies.set(i, lobby);
                } else {
                    lobbies.add(lobby);
                }
            }
        }

        System.out.println("Lobbies initilized!");
    }

    /**
     * Ensures at runtime that we always have 4 active lobbies
     */
    public void maintainLobbies() {
        for (int i = 0; i < 4; i++) {
            if (lobbies.get(i) == null) {
                Player dummyHost = new Player();
                dummyHost.setId(-1L);
                dummyHost.setUsername("Host_" + (i + 1));
                dummyHost.setIsHost(true);
                dummyHost.setIsReady(false);

                lobbies.set(i, lobbyService.createRoom(dummyHost));
            }
        }
    }

    public List<GameRoom> getLobbies() {
        return lobbies;
    }
}
