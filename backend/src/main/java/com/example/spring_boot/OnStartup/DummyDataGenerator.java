package com.example.spring_boot.OnStartup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DummyDataGenerator implements CommandLineRunner {
    
    private final GenerateTriviaDb gen;
    private final CreateLobbies lobbies;

    public DummyDataGenerator(GenerateTriviaDb gen, CreateLobbies lobbies){
        this.gen = gen;
        this.lobbies = lobbies;

    }
    public void run(String... args) throws Exception {
        gen.populate();
        lobbies.initLobbies();
        System.out.println("lobbies initilized succesfully");
        
    }


}
