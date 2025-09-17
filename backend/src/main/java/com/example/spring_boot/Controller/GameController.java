package com.example.spring_boot.Controller;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private QuestionRepository questionRepository;



    @PostMapping("/check-answer")
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

    @PostMapping("/check-answers")
    public ResponseEntity<?> checkAnswers(@RequestBody Map<String, Object> request) {
        try {
            List<Integer> questionIds = (List<Integer>)request.get("questionIds");
            List<String> answers = (List<String>) request.get("answers");

            if (questionIds.size() != answers.size()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Mismatched questionIds and answers length"));
            }

            List<Boolean> corrects = new java.util.ArrayList<>();

            for (int i = 0; i < questionIds.size(); i++) {
                Long qId = Long.valueOf(questionIds.get(i));
                String ans = answers.get(i);

                Map<String, Object> result = evaluateAnswer(qId, ans);
                corrects.add((Boolean) result.getOrDefault("correct", false));
            }

            // Response structure matches your Kotlin expectation
            Map<String, Object> response = Map.of("corrects", corrects);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to check answers: " + e.getMessage()));
        }
    }

    private Map<String, Object> evaluateAnswer(Long qId, String ans) {
        Optional<Question> questionOpt = questionRepository.findById(qId);
        if (questionOpt.isEmpty()) {
            return Map.of(
                    "questionId", qId,
                    "error", "Question not found"
            );
        }

        Question question = questionOpt.get();
        boolean isCorrect = question.getCorrectAnswer().equals(ans);

        return Map.of(
                "questionId", qId,
                "correct", isCorrect,
                "correctAnswer", question.getCorrectAnswer()
        );
    }



}
