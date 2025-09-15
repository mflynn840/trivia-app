package com.example.spring_boot.Model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int points;

    // Relationship: A score belongs to a player (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)  // foreign key to the Player table
    private Player player;

    @Column(nullable = false)
    private LocalDateTime time;

    // Default constructor
    public Score() {}

    // Constructor with parameters
    public Score(int points, Player player, LocalDateTime time) {
        this.points = points;
        this.player = player;
        this.time = time;
    }

    // Getters and Setters
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    // Override toString() for better debugging
    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", points=" + points +
                ", player=" + player.getId() +  // Assuming player has an 'id' field
                ", time=" + time +
                '}';
    }
}
