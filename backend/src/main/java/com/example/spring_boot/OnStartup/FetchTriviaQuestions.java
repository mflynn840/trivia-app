package com.example.spring_boot.OnStartup;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.io.FileWriter;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.json.JSONArray;
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

    // Fetch trivia questions from the Open Trivia Database API
    public void fetchAndSaveTriviaQuestions() {
        int totalQuestions = 0;
        int page = 1;
        JSONArray allQuestions = new JSONArray();

        // Loop to fetch questions until we reach 1000 questions
        while (totalQuestions < 1000) {
            String url = String.format("%s?amount=50&type=multiple&page=%d", apiUrl, page);
            ResponseEntity<String> response = null;

            try {
                response = restTemplate.getForEntity(url, String.class);
            } catch (Exception e) {
                logger.error("Error making request to URL: {}", url, e);
                break; // Exit the loop if there's a network error or other failure
            }

            // Check if the response is successful
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                JSONObject triviaData = new JSONObject(response.getBody());
                JSONArray questions = triviaData.getJSONArray("results");

                // If we have questions, add them to the list
                if (questions.length() > 0) {
                    allQuestions.put(questions);
                    totalQuestions += questions.length();
                    page++;  // Move to the next page of questions
                    logger.info("Fetched {} questions from page {}. Total questions: {}", questions.length(), page - 1, totalQuestions);
                } else {
                    logger.info("No more questions available after page {}.", page - 1);
                    break;
                }
            } else {
                logger.error("Failed to fetch questions from API. Response: {} {}", response.getStatusCode(), response.getBody());
                break;
            }

            // Optional: Add delay between requests to avoid overloading the API
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted during sleep. Continuing the process.");
            }
        }

        // After collecting 1000 questions, save them to a file
        if (totalQuestions >= 1000) {
            saveQuestionsToFile(allQuestions);
        }
    }

    // Save fetched questions to a JSON file
    private void saveQuestionsToFile(JSONArray allQuestions) {
        try {
            // Make sure the questions directory exists
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(questionsDir));

            // Create a file and write all questions to it
            try (FileWriter file = new FileWriter(questionsDir + "/all_questions_1000.json")) {
                file.write(allQuestions.toString(4)); // Indent JSON for readability
                logger.info("Saved {} questions to all_questions_1000.json", allQuestions.length());
            } catch (IOException e) {
                logger.error("Error writing to file {}: {}", questionsDir + "/all_questions_1000.json", e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Error creating directories for the questions file: {}", e.getMessage());
        }
    }
}
