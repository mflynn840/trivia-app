package com.example.spring_boot.OnStartup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DummyDataGenerator implements CommandLineRunner {
    
    private final GenerateTriviaDb gen;
    private final ComputeJson test;

    public DummyDataGenerator(GenerateTriviaDb gen, ComputeJson t){
        this.gen = gen;
        this.test = t;

    }
    public void run(String... args) throws Exception {
        gen.populate();
        System.out.println("lobbies initilized succesfully");
        
    }


}
