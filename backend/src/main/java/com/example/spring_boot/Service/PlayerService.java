package com.example.spring_boot.Service;

import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

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

        // Set profile picture
        player.setProfilePicture(profilePictureBytes);

        // Save the player object with the profile picture
        return playerRepository.save(player);
    }
}
