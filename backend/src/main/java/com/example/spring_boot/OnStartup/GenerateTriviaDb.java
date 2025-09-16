package com.example.spring_boot.OnStartup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.spring_boot.Repository.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import com.example.spring_boot.Model.Question;

/**
 * This script fills in the Question repository with json loaded trivia question data
 */
@Component
public class GenerateTriviaDb {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void populate(){
        this.questionRepository.deleteAll();
        try{
            this.saveTriviaQuestions();
        }catch(java.io.IOException e){
            System.err.println("INVALID TRIVIA JSON");
        }
        this.questionRepository.flush();
        System.out.println("Trivia Data populated");
    }

    public void saveTriviaQuestions() throws java.io.IOException {

        // Load the trivia questions JSON file
        try {
            File jsonFile = new File("questions.json"); // Adjust path if needed
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            JsonNode resultsNode = rootNode.path("results");

            //parse the JSON to get the question data
            for (JsonNode questionNode : resultsNode) {
                //collect the data for a single Question object from the json
                String questionText = questionNode.path("question").asText("");
                String correctAnswer = questionNode.path("correct_answer").asText("");
                String category = questionNode.path("category").asText("");
                String difficulty = questionNode.path("difficulty").asText("");
                String type = questionNode.path("type").asText("");


                List<String> incorrectAnswers = new ArrayList<>();
                questionNode.path("incorrect_answers").forEach(answer -> {
                    String ans = answer.asText("");
                    incorrectAnswers.add(ans);
                });

                List<String> allAnswers = new ArrayList<>(incorrectAnswers);
                allAnswers.add(correctAnswer);

                // Ensure at least 4 options
                while (allAnswers.size() < 4) allAnswers.add("");

                //randomize which option is correct
                Collections.shuffle(allAnswers);

                String optionA = allAnswers.get(0);
                String optionB = allAnswers.get(1);
                String optionC = allAnswers.get(2);
                String optionD = allAnswers.get(3);




                Question question = new Question(
                        questionText, 
                        correctAnswer, 
                        optionA, 
                        optionB, 
                        optionC, 
                        optionD, 
                        category, 
                        difficulty, 
                        type
                );
                // Save the question to the repository
                questionRepository.save(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading the questions JSON file: " + e.getMessage());
        }
    }


}
