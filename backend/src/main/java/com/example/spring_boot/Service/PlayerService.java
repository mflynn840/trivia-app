package com.example.spring_boot.Service;

import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import org.springframework.security.core.Authentication;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    // Method to save player's profile picture
    public Player savePlayerProfilePicture(Long playerId, MultipartFile profilePictureFile) throws IOException {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Convert image to byte array
        byte[] profilePictureBytes = profilePictureFile.getBytes();


        // Save the player object with the profile picture
        return playerRepository.save(player);
    }

    public Player getAuthenticatedPlayer(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Extract the username from the authentication object
            String username = authentication.getName();

            // Fetch the player from the database based on the username
            return playerRepository.findByUsername(username);
        }
        return null; // Return null if no user is authenticated
    }

    
}
