package com.example.spring_boot.Service;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Repository.QuestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class JsonService {

    @Autowired
    private QuestionRepository questionRepository;

    public void loadQuestionsFromFileAndSaveToDatabase(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Read the JSON file
            JsonNode rootNode = objectMapper.readTree(new File(filePath));
            JsonNode resultsNode = rootNode.get("results");

            // Iterate through each question in the results
            int id = 1;
            for (JsonNode questionNode : resultsNode) {
                String questionText = questionNode.get("question").asText();
                String correctAnswer = questionNode.get("correct_answer").asText();
                List<String> incorrectAnswers = new ArrayList<>();
                for (JsonNode incorrectAnswerNode : questionNode.get("incorrect_answers")) {
                    incorrectAnswers.add(incorrectAnswerNode.asText());
                }

                // Create a list of answers (correct + incorrect)
                List<String> allAnswers = new ArrayList<>();
                allAnswers.add(correctAnswer);
                allAnswers.addAll(incorrectAnswers);

                // Randomize the order of answers
                Collections.shuffle(allAnswers, new Random());

                // Create the question object and assign options
                Question question = new Question();
                question.setId((long) id++);
                question.setQuestion(questionText);

                question.setOptionA(allAnswers.get(0));
                question.setOptionB(allAnswers.get(1));
                question.setOptionC(allAnswers.get(2));
                question.setOptionD(allAnswers.get(3));

                // Set the correct answer
                question.setCorrectAnswer(correctAnswer);

                // Save the question into the database
                questionRepository.save(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
