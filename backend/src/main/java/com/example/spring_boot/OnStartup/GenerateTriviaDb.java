package com.example.spring_boot.OnStartup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.spring_boot.Repository.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.spring_boot.Model.Question;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


/**
 * This script fills in the Question repository with json loaded trivia question data
 */
@Component
public class GenerateTriviaDb {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * parse each json in the folder and save it to the db
     * JSONS are assumed to be returned from 
     *
     */
    public void populate(String folderName) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // load all resources inside "resources/folderName"
            Resource[] resources = resolver.getResources("classpath:" + folderName + "/*");

            Arrays.stream(resources)
                    .filter(Resource::exists)
                    .forEach(resource -> {
                        System.out.println("Adding contents of file " + resource.getFilename());
                        try (InputStream in = resource.getInputStream()) {
                            saveTriviaQuestions(in); // rewrite saveTriviaQuestions to accept InputStream
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            this.questionRepository.flush();
            System.out.println("Trivia Data populated");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTriviaQuestions(InputStream jsonInput) {
        try {
            // Load the trivia questions JSON file from the InputStream
            JsonNode rootNode = objectMapper.readTree(jsonInput);
            JsonNode resultsNode = rootNode.path("results");

            // Parse the JSON to get the question data
            for (JsonNode questionNode : resultsNode) {
                String questionText = StringEscapeUtils.unescapeHtml4(
                        questionNode.path("question").asText(""));
                String correctAnswer = StringEscapeUtils.unescapeHtml4(
                        questionNode.path("correct_answer").asText(""));
                String category = StringEscapeUtils.unescapeHtml4(
                        questionNode.path("category").asText(""));
                String difficulty = StringEscapeUtils.unescapeHtml4(
                        questionNode.path("difficulty").asText(""));
                if (!difficulty.isEmpty()) {
                    difficulty = difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1);
                }
                String type = StringEscapeUtils.unescapeHtml4(
                        questionNode.path("type").asText(""));

                List<String> incorrectAnswers = new ArrayList<>();
                questionNode.path("incorrect_answers").forEach(answer -> {
                    incorrectAnswers.add(StringEscapeUtils.unescapeHtml4(answer.asText("")));
                });

                List<String> allAnswers = new ArrayList<>(incorrectAnswers);
                allAnswers.add(correctAnswer);

                // Ensure at least 4 options
                while (allAnswers.size() < 4) allAnswers.add("");

                // Randomize option order
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
                questionRepository.save(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading the questions JSON: " + e.getMessage());
        } 
    }
}
