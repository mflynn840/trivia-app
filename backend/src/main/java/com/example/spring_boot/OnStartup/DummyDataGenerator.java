package com.example.spring_boot.OnStartup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DummyDataGenerator implements CommandLineRunner {
    
    private final GenerateTriviaDb gen;

    public DummyDataGenerator(GenerateTriviaDb gen){
        this.gen = gen;

    }
    public void run(String... args) throws Exception {
        gen.populate();
        
    }


}
