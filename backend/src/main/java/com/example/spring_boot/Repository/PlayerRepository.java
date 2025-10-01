package com.example.spring_boot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot.Model.user.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    public Player findByUsername(String username);

}
