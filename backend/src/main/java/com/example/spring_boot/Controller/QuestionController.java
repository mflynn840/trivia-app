package com.example.spring_boot.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.spring_boot.Service.QuestionService;
import com.example.spring_boot.Model.Question;

import java.util.List;

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
    
    @GetMapping("/random")
    public ResponseEntity<List<Question>> getRandomQuestions(@RequestParam(defaultValue = "5") int count){
        try{
            List<Question> questions = this.questionService.getRandomQuestions(count);
            return ResponseEntity.ok(questions);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
}
