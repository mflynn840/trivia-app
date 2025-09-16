package com.example.spring_boot.Controller;

import com.example.spring_boot.Model.Player;
import com.example.spring_boot.Repository.PlayerRepository;
import com.example.spring_boot.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
            }

            // Check if user already exists
            Player existingPlayer = playerRepository.findByUsername(username);
            if (existingPlayer != null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }

            // Create new user
            Player player = new Player();
            player.setUsername(username);
            player.setPassword(passwordEncoder.encode(password));
            player.setRole(com.example.spring_boot.Model.Role.USER);

            playerRepository.save(player);

            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
            }

            // Find user
            Player player = playerRepository.findByUsername(username);
            if (player == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
            }

            // Check password
            if (!passwordEncoder.matches(password, player.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(player.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", player.getUsername());
            response.put("role", player.getRole().name());
            response.put("id", player.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && jwtUtil.validateToken(token, username)) {
                Player player = playerRepository.findByUsername(username);
                if (player != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", player.getUsername());
                    response.put("role", player.getRole().name());
                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("valid", false, "error", "Token validation failed: " + e.getMessage()));
        }
    }
}
