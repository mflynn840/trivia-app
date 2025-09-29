package com.example.spring_boot.OnStartup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class UpdateQuestionDb implements CommandLineRunner {
    
    private final GenerateTriviaDb gen;

    public UpdateQuestionDb(GenerateTriviaDb gen, FetchTriviaQuestions fetch){
        this.gen = gen;

    }
    public void run(String... args) throws Exception {

        System.out.println("Populating Trivia DB from resources/foo...");
        gen.populate("questions"); // This will handle loading all JSONs from resources/foo
        System.out.println("Trivia DB population complete.");
    }

}
