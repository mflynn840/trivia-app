package com.example.spring_boot.Controller;

import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Service.PlayerService;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // Endpoint for uploading a profile picture
    @PostMapping("/{playerId}/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
            @PathVariable Long playerId, 
            @RequestParam("file") MultipartFile file) {

        try {
            // Save the profile picture and update the player in the database
            playerService.savePlayerProfilePicture(playerId, file);

            // Return success message
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Profile picture uploaded successfully!");

        } catch (IOException e) {
            // Handle any exceptions (e.g., file conversion issues)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload profile picture: " + e.getMessage());
        } catch (Exception e) {
            // Catch other exceptions (e.g., player not found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Player not found with ID: " + playerId);
        }
    }

}
