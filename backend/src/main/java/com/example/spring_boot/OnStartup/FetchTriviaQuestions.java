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

    public FetchTriviaQuestions(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void run() {
        try {
            fetchAndSaveTriviaQuestions();
        } catch (Exception e) {
            logger.error("An error occurred while fetching and saving trivia questions: ", e);
        }
        // Removed System.exit(0) to allow app to continue running
    }

    public void fetchAndSaveTriviaQuestions() {
        JSONArray allQuestions = new JSONArray();

        String url = String.format("%s?amount=50&type=multiple", apiUrl);

        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(url, String.class);
            logger.info("API Response: {}", response.getBody());
        } catch (Exception e) {
            logger.error("Error making request to URL: {}", url, e);
            return;
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JSONObject triviaData = new JSONObject(response.getBody());

                int responseCode = triviaData.getInt("response_code");
                if (responseCode != 0) {
                    logger.warn("Trivia API returned non-success response code: {}", responseCode);
                    return;
                }

                JSONArray questions = triviaData.getJSONArray("results");
                if (questions.length() > 0) {
                    // Merge questions into allQuestions
                    for (int i = 0; i < questions.length(); i++) {
                        allQuestions.put(questions.getJSONObject(i));
                    }
                    logger.info("Fetched {} questions.", questions.length());
                    saveQuestionsToFile(allQuestions);
                } else {
                    logger.info("No questions available in the response.");
                }
            } catch (JSONException e) {
                logger.error("Error parsing JSON response: {}", e.getMessage());
            }
        } else {
            logger.error("Failed to fetch questions from API. Status: {}", response.getStatusCode());
        }
    }

    private void saveQuestionsToFile(JSONArray allQuestions) {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(questionsDir));
            try (FileWriter file = new FileWriter(questionsDir + "/all_questions_50.json")) {
                file.write(allQuestions.toString(4)); // Pretty-print JSON
                logger.info("Saved {} questions to all_questions_50.json", allQuestions.length());
            }
        } catch (IOException e) {
            logger.error("Error writing questions file: {}", e.getMessage());
        }
    }
}
