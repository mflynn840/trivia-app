package com.example.spring_boot.Model.user;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

/**
 * Define a user data structure
 */
@Entity
@Table(name = "users")
public class Player implements UserDetails {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Attributes

    @Column(nullable=false, unique=true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    private Role role;

    @JsonProperty("isReady")
    private boolean isReady = false;

    byte[] profilePicture;


    private Long score = (long) 0;

    // Constructors
    public Player() {}

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = Role.PLAYER;
        
    }

    // Getters and setters
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return this.username; }
    public void setUsername(String username) { this.username = username; }

    public Role getRole() { return this.role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    public Long getScore(){return this.score;}
    public void setScore(Long score){this.score = score;}

    public byte[] getProfilePicture(){return this.profilePicture;}
    public void setProfilePicture(byte[] profilePicture){this.profilePicture = profilePicture;}



    // Return a list of the user's privileges
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role != null){
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }else{
            return Collections.emptyList();
        }
        
    }

    // Implement other required methods from UserDetails interface
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setReady(boolean b) {this.isReady = b;}
    public boolean isReady(){return this.isReady;}




}
