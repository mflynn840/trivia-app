package com.example.spring_boot.Controller;

import com.example.spring_boot.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    // Response wrapper
    static class ApiResponse {
        private String message;
        public ApiResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @PostMapping("/{username}/upload-profile-picture")
    public ResponseEntity<ApiResponse> uploadProfilePicture(
            @PathVariable String username, 
            @RequestParam("file") MultipartFile file,
            @RequestHeader Map<String, String> headers) {

        try {
            playerService.savePlayerProfilePicture(username, file);
            return ResponseEntity.ok(new ApiResponse("Profile picture uploaded successfully!"));

        } catch (IOException e) {
            logger.error("IOException while uploading profile picture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to upload profile picture: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Player not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Player not found with username: " + username));
        } catch (IllegalStateException e){
            logger.error("Invalid image file", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Image not found or invalid: " + file.getOriginalFilename()));
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping("/{username}/get-profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username) {
        try {
            byte[] avatarBytes = playerService.getAvatarBytes(username);

            if (avatarBytes == null || avatarBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg"); 
            headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(avatarBytes.length));

            return new ResponseEntity<>(avatarBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching profile picture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
