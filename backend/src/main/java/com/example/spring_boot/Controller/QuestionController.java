package com.example.spring_boot.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.spring_boot.Service.QuestionService;
import com.example.spring_boot.Model.Question;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*") // Allow CORS for mobile app
public class QuestionController {

    QuestionService questionService;

    public QuestionController(QuestionService questionService){
        this.questionService = questionService;
    }

    @GetMapping("/{idx}")
    public ResponseEntity<?> getQuestion(@PathVariable("idx") int index){
        try{
            return ResponseEntity.ok(this.questionService.getQuestion(index));
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/randoms/{count}/{category}/{difficulty}")
    public ResponseEntity<List<Question>> getRandomQuestions(@RequestParam int count,
                                            @RequestParam String category,
                                            @RequestParam String difficulty){
        try{
            List<Question> questions = this.questionService.getRandomQuestions(count, category,null);
            return ResponseEntity.ok(questions);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuestion() {
        try {
            Question question = questionService.getRandomQuestion();
            if (question != null) {
                return ResponseEntity.ok(question);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch random question: " + e.getMessage()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getQuestionCount(){
        try{
            long count = this.questionService.getQuestionCount();
            return ResponseEntity.ok(count);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get_by/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        try {
            Optional<Question> question = questionService.findById(id);
            if (question.isPresent()) {
                return ResponseEntity.ok(question.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch question: " + e.getMessage()));
        }
    }

    @GetMapping("/counts_by_category")
    public ResponseEntity<Map<String, Map<String, Long>>> getQuestionCountsByCategoryAndDifficulty() {
        Map<String, Map<String, Long>> counts = questionService.getQuestionCountsByCategoryAndDifficulty();
        return ResponseEntity.ok(counts);
    }




}
