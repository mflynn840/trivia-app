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
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Question getQuestion(int index){
        if(index > this.questionRepository.count()){
            throw new IllegalArgumentException("index out of bounds");
        }
        return this.questionRepository.findByRowNumber(index);
    }

    public int loadQuestionsFromJson() throws IOException {
        File jsonFile = new File("questions.json");
        if (!jsonFile.exists()) {
            throw new IOException("questions.json file not found");
        }

        JsonNode rootNode = objectMapper.readTree(jsonFile);
        JsonNode results = rootNode.get("results");

        if (results == null || !results.isArray()) {
            throw new IOException("Invalid JSON format");
        }

        int loadedCount = 0;
        for (JsonNode questionNode : results) {
            try {
                Question question = parseQuestionFromJson(questionNode);
                questionRepository.save(question);
                loadedCount++;
            } catch (Exception e) {
                System.err.println("Error parsing question: " + e.getMessage());
            }
        }

        return loadedCount;
    }

    private Question parseQuestionFromJson(JsonNode questionNode) {
        String questionText = questionNode.get("question").asText();
        String correctAnswer = questionNode.get("correct_answer").asText();
        String category = questionNode.get("category").asText();
        String difficulty = questionNode.get("difficulty").asText();
        String type = questionNode.get("type").asText();

        // Parse incorrect answers
        List<String> incorrectAnswers = new ArrayList<>();
        JsonNode incorrectAnswersNode = questionNode.get("incorrect_answers");
        if (incorrectAnswersNode != null && incorrectAnswersNode.isArray()) {
            for (JsonNode answerNode : incorrectAnswersNode) {
                incorrectAnswers.add(answerNode.asText());
            }
        }

        return new Question(questionText, correctAnswer, incorrectAnswers, category, difficulty, type);
    }

    public Question getRandomQuestion(String difficulty, String category) {
        if (difficulty != null && category != null) {
            return questionRepository.findRandomQuestionByDifficultyAndCategory(difficulty, category);
        } else if (difficulty != null) {
            return questionRepository.findRandomQuestionByDifficulty(difficulty);
        } else if (category != null) {
            return questionRepository.findRandomQuestionByCategory(category);
        } else {
            return questionRepository.findRandomQuestion();
        }
    }

    public List<Question> getQuestions(int count, String difficulty, String category) {
        List<Question> questions;
        
        if (difficulty != null && category != null) {
            questions = questionRepository.findByDifficultyAndCategory(difficulty, category);
        } else if (difficulty != null) {
            questions = questionRepository.findByDifficulty(difficulty);
        } else if (category != null) {
            questions = questionRepository.findByCategory(category);
        } else {
            questions = questionRepository.findAll();
        }

        // Shuffle and limit results
        Collections.shuffle(questions, new Random());
        if (questions.size() > count) {
            questions = questions.subList(0, count);
        }

        return questions;
    }

    public boolean checkAnswer(Long questionId, String answer) {
        return questionRepository.findById(questionId)
                .map(question -> question.getCorrectAnswer().equals(answer))
                .orElse(false);
    }
}
