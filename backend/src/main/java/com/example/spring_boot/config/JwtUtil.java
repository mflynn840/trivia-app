package com.example.spring_boot.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
/**
 * 
 * A utility class for handling JWT authentication tokens
 */
@Component
public class JwtUtil {

    //encrpytion key (set in prod to a real key)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /** Encrypt a 1 day JWT token from username */
    public String generateToken(String username){
        return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    //token expires in one day
                    .setExpiration(new Date(System.currentTimeMillis()+86400000))
                    .signWith(key)
                    .compact(); 
    }

    /** extract username by decrypting and parsing the token*/
    public String extractUsername(String token){
        return Jwts.parserBuilder().setSigningKey(this.key).build()
                    .parseClaimsJws(token).getBody().getSubject();
    }
    
    /** extract username by decrypting and parsing the token*/
    String getUsername(String token){
        return extractUsername(token);
    }

    /** Tokens are valid when they were generated from
     * this username and are not expired
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        return getUsername(token).equals(userDetails.getUsername()) && !isExpired(token);
    }
    
    /** Validate token with username */
    public boolean validateToken(String token, String username){
        return extractUsername(token).equals(username) && !isExpired(token);
    }

    /** extract expiration time (millis since epoch) by decrypting token */
    private boolean isExpired(String token){
        return Jwts.parserBuilder().setSigningKey(this.key).build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
