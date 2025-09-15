package com.example.spring_boot.Repository;

import com.example.spring_boot.Model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    public Player findByUsername(String username);

}
