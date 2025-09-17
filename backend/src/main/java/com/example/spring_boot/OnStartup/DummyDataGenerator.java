package com.example.spring_boot.OnStartup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DummyDataGenerator implements CommandLineRunner {
    
    private final GenerateTriviaDb gen;
    private final FetchTriviaQuestions fetch;

    public DummyDataGenerator(GenerateTriviaDb gen, FetchTriviaQuestions fetch){
        this.fetch = fetch;
        this.gen = gen;

    }
    public void run(String... args) throws Exception {
        gen.populate();
        fetch.run();
        
    }


}
