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
    public Player savePlayerProfilePicture(String username, MultipartFile profilePictureFile) throws IOException {
        System.out.println("finding player");
        Player player = playerRepository.findByUsername(username);

        if(player == null){
            throw new IllegalArgumentException("Player not found");
        }
        System.out.println("Unpacking profile picture");
        // Convert image to byte array
        byte[] profilePictureBytes = profilePictureFile.getBytes();
        if(profilePictureBytes.length == 0){
            throw new IllegalStateException("No image recieved");
        }
        player.setProfilePicture(profilePictureBytes);

        // Save the player object with the profile picture
        playerRepository.save(player);
        playerRepository.flush();
        return player;
        
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

    public byte[] getAvatarBytes(String username) {
        Player p = this.playerRepository.findByUsername(username);
        if (p == null) {
            throw new IllegalArgumentException("Player not found: " + username);
        }

        byte[] profilePicture = p.getProfilePicture();
        if (profilePicture == null || profilePicture.length == 0) {
            throw new IllegalStateException("Player has no profile picture: " + username);
        }

        return profilePicture;
    }


    
}
