package com.example.spring_boot.OnStartup;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import java.io.FileWriter;
import java.io.IOException;
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
            String token = getSessionToken();
            fetchAndSaveTriviaQuestions(20, token); // Start with 20 questions
        } catch (Exception e) {
            logger.error("Error in FetchTriviaQuestions run method: ", e);
        }
    }

    // Request a session token from Open Trivia DB
    private String getSessionToken() {
        String tokenUrl = "https://opentdb.com/api_token.php?command=request";
        try {
            String responseBody = restTemplate.getForObject(tokenUrl, String.class);
            JSONObject obj = new JSONObject(responseBody);
            return obj.getString("token");
        } catch (Exception e) {
            logger.warn("Failed to get session token, continuing without it. Error: {}", e.getMessage());
            return null; // Token is optional
        }
    }

    private void fetchAndSaveTriviaQuestions(int amount, String token) {
        JSONArray allQuestions = new JSONArray();
        String url = apiUrl + "?amount=" + amount + "&type=multiple";
        if (token != null) url += "&token=" + token;

        try {
            logger.info("Requesting {} questions from Trivia API...", amount);
            String responseBody = restTemplate.getForObject(url, String.class);
            JSONObject triviaData = new JSONObject(responseBody);

            int responseCode = triviaData.getInt("response_code");

            switch (responseCode) {
                case 0: // Success
                    JSONArray questions = triviaData.getJSONArray("results");
                    for (int i = 0; i < questions.length(); i++) {
                        allQuestions.put(questions.getJSONObject(i));
                    }
                    logger.info("Fetched {} questions successfully.", questions.length());
                    saveQuestionsToFile(allQuestions);
                    break;

                case 2: // Not enough questions
                    if (amount > 1) {
                        int nextAmount = Math.max(amount / 2, 1);
                        logger.warn("Not enough questions for amount {}. Retrying with {}...", amount, nextAmount);
                        Thread.sleep(500); // small delay to avoid hitting rate limit
                        fetchAndSaveTriviaQuestions(nextAmount, token);
                    }
                    break;

                case 5: // Token empty / rate limit
                    logger.warn("Rate limited by Trivia API (response_code 5). Retrying in 2 seconds...");
                    Thread.sleep(2000);
                    fetchAndSaveTriviaQuestions(amount, token);
                    break;

                default:
                    logger.warn("Trivia API returned response_code={} with no questions.", responseCode);
                    break;
            }

        } catch (HttpClientErrorException.TooManyRequests e) {
            logger.warn("HTTP 429 Too Many Requests. Retrying in 2 seconds...");
            try { Thread.sleep(2000); } catch (InterruptedException ex) { /* ignore */ }
            fetchAndSaveTriviaQuestions(amount, token);

        } catch (JSONException e) {
            logger.error("Error parsing JSON response: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching questions from API: {}", e.getMessage());
        }
    }

    private void saveQuestionsToFile(JSONArray allQuestions) {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(questionsDir));
            try (FileWriter file = new FileWriter(questionsDir + "/all_questions.json")) {
                file.write(allQuestions.toString(4));
                logger.info("Saved {} questions to all_questions.json", allQuestions.length());
            }
        } catch (IOException e) {
            logger.error("Error writing questions file: {}", e.getMessage());
        }
    }
}
