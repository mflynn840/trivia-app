package com.example.spring_boot.Controller;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Repository.QuestionRepository;
import com.example.spring_boot.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String category) {
        try {
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

            // Limit results and shuffle
            if (questions.size() > count) {
                questions = questions.subList(0, count);
            }

            return ResponseEntity.ok(Map.of("questions", questions));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch questions: " + e.getMessage()));
        }
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        try {
            Optional<Question> question = questionRepository.findById(id);
            if (question.isPresent()) {
                return ResponseEntity.ok(question.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch question: " + e.getMessage()));
        }
    }

    @PostMapping("/questions/load")
    public ResponseEntity<?> loadQuestionsFromJson() {
        try {
            int loadedCount = questionService.loadQuestionsFromJson();
            return ResponseEntity.ok(Map.of("message", "Questions loaded successfully", "count", loadedCount));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to load questions: " + e.getMessage()));
        }
    }

    @GetMapping("/questions/random")
    public ResponseEntity<?> getRandomQuestion(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String category) {
        try {
            Question question = questionService.getRandomQuestion(difficulty, category);
            if (question != null) {
                return ResponseEntity.ok(question);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch random question: " + e.getMessage()));
        }
    }

    @PostMapping("/questions/check-answer")
    public ResponseEntity<?> checkAnswer(@RequestBody Map<String, Object> request) {
        try {
            Long questionId = Long.valueOf(request.get("questionId").toString());
            String answer = request.get("answer").toString();

            Optional<Question> questionOpt = questionRepository.findById(questionId);
            if (questionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Question not found"));
            }

            Question question = questionOpt.get();
            boolean isCorrect = question.getCorrectAnswer().equals(answer);

            Map<String, Object> response = new HashMap<>();
            response.put("correct", isCorrect);
            response.put("correctAnswer", question.getCorrectAnswer());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to check answer: " + e.getMessage()));
        }
    }
}
