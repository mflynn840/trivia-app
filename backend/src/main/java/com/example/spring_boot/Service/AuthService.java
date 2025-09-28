package com.example.spring_boot.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Repository.PlayerRepository;
import com.example.spring_boot.config.JwtUtil;

@Service
public class AuthService {
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void registerUser(String username, String password){

        //check that the request is valid
        if (username == null || 
            password == null || 
            username.trim().isEmpty() || 
            password.trim().isEmpty()
        ) {
            throw new IllegalArgumentException();
        }

        // Check if user already exists
        Player existingPlayer = playerRepository.findByUsername(username);
        if (existingPlayer != null) {
            
        }
        // Create new user
        Player player = new Player();
        player.setUsername(username);
        player.setPassword(passwordEncoder.encode(password));
        player.setRole(com.example.spring_boot.Model.Role.PLAYER);
        player.setProfilePicture(loadDefaultPicture());
        playerRepository.save(player);
    }

    public Map<String, Object> login(String username, String password){

        // malformed request
        if (username == null || 
            password == null || 
            username.trim().isEmpty() || 
            password.trim().isEmpty()
        ) {
            throw new IllegalArgumentException("Malformed request");
        }

        // Find user
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            throw new IllegalStateException("User does not exist");
        }

        // Check password
        if (!passwordEncoder.matches(password, player.getPassword())) {
            throw new IllegalStateException("Invalid credentials");
        }

        // Generate and return JWT token
        String jwtToken = jwtUtil.generateToken(player.getUsername());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);
        responseBody.put("username", player.getUsername());
        responseBody.put("role", player.getRole().name());
        responseBody.put("id", player.getId());

        return responseBody;
    }

    public Map<String, Object> validateToken(String authHeader){
        
        //handle malformed request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Malformed request");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        
        if (username == null) {
            throw new IllegalArgumentException("Malformed request");
        }

        Player player = playerRepository.findByUsername(username);

        //user doesnt exist
        if (player == null) {
            throw new IllegalStateException("Validation failed");
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("valid", true);
        responseBody.put("username", player.getUsername());
        responseBody.put("role", player.getRole().name());
        return responseBody;
    }


    private byte[] loadDefaultPicture() {
        try{
            InputStream inputStream = getClass().getResourceAsStream("/default-profile.webp");
            if (inputStream == null) {
                throw new IllegalStateException("Default avatar image not found");
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            System.out.println("FAiled to load");
            throw new RuntimeException("Failed to load default avatar", e);
        }
    }

}
