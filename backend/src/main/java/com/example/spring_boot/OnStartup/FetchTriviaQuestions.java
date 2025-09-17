package com.example.spring_boot.OnStartup;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.io.FileWriter;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FetchTriviaQuestions {

    private static final Logger logger = LoggerFactory.getLogger(FetchTriviaQuestions.class);

    private final RestTemplate restTemplate;

    @Value("${trivia.api.url}")
    private String apiUrl;

    @Value("${trivia.questions.dir}")
    private String questionsDir;

    // Constructor to initialize RestTemplate
    public FetchTriviaQuestions(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void run() {
        try {
            fetchAndSaveTriviaQuestions();
        } catch (Exception e) {
            logger.error("An error occurred while fetching and saving trivia questions: ", e);
        }
    }

    // Fetch 50 trivia questions from the Open Trivia Database API
    public void fetchAndSaveTriviaQuestions() {
        int totalQuestions = 0;
        JSONArray allQuestions = new JSONArray();

        String url = String.format("%s?amount=50&type=multiple", apiUrl); // Query 50 questions at once

        ResponseEntity<String> response = null;

        try {
            response = restTemplate.getForEntity(url, String.class);
            logger.info("API Response: {}", response.getBody());  // Log the raw response
        } catch (Exception e) {
            logger.error("Error making request to URL: {}", url, e);
        }

        // Ensure the response is successful
        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            try {
                JSONObject triviaData = new JSONObject(response.getBody());
                JSONArray questions = triviaData.getJSONArray("results");

                if (questions.length() > 0) {
                    allQuestions.put(questions);  // Store the questions in allQuestions array
                    totalQuestions += questions.length();
                    logger.info("Fetched {} questions. Total questions: {}", questions.length(), totalQuestions);
                } else {
                    logger.info("No questions available in the response.");
                }
            } catch (JSONException e) {
                logger.error("Error parsing JSON response: {}", e.getMessage());
            }
        } else {
            logger.error("Failed to fetch questions from API. Response: {} {}", response.getStatusCode(), response.getBody());
        }

        // Save the fetched questions to a file if at least one question is fetched
        if (totalQuestions > 0) {
            saveQuestionsToFile(allQuestions);
        }
    }

    // Save fetched questions to a JSON file
    private void saveQuestionsToFile(JSONArray allQuestions) {
        try {
            // Make sure the questions directory exists
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(questionsDir));

            // Create a file and write all questions to it
            try (FileWriter file = new FileWriter(questionsDir + "/all_questions_50.json")) {
                file.write(allQuestions.toString(4)); // Indent JSON for readability
                logger.info("Saved {} questions to all_questions_50.json", allQuestions.length());
            } catch (IOException e) {
                logger.error("Error writing to file {}: {}", questionsDir + "/all_questions_50.json", e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Error creating directories for the questions file: {}", e.getMessage());
        }
    }
}
