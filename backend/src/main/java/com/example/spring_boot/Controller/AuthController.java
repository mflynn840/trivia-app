package com.example.spring_boot.Controller;

import com.example.spring_boot.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            try{
                authService.registerUser(username, password);
            }catch(IllegalArgumentException e){
                return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
            }catch(IllegalStateException e){
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }

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

            Map<String, Object> responseBody = authService.login(username, password);
            System.out.println("sucessful login");
            return ResponseEntity.ok(responseBody);
        } catch(IllegalArgumentException e){
            System.out.println("login failed: invalid credentials");
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }catch(IllegalStateException e){
            System.out.println("login failed: invalid credentials");
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
            
        }
    }

    /**
     * 
     * @param authHeader
     * @return
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            Map<String, Object> responseBody = authService.validateToken(authHeader);
            return ResponseEntity.ok(responseBody);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
        } catch (IllegalStateException e){
            return ResponseEntity.internalServerError().body(Map.of("valid", false, "error", "Token validation failed: " + e.getMessage()));
        }
    }
}
