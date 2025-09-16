package com.example.spring_boot.Service;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

// Add these imports at the top of QuestionService.java
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.domain.PageRequest;


@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    private Random random = new Random();

    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    public List<Question> getRandomQuestions(int count, String category, String difficulty) {
        return questionRepository.findRandomQuestions(count, category, difficulty);
    }


    public boolean checkAnswer(Long questionId, String answer) {
        return questionRepository.findById(questionId)
                .map(q -> q.getCorrectAnswer().equalsIgnoreCase(answer))
                .orElse(false);
    }
    
    
    public Question getQuestion(int index){
        if(index > this.questionRepository.count() || index < 1){
            throw new IllegalArgumentException("index out of bounds");
        }
        return this.questionRepository.findByRowNumber(index - 1); // Convert to 0-based
    }
    
    public long getQuestionCount() {
        return this.questionRepository.count();
    }

    public Question getRandomQuestion(){
        return getRandomQuestions(1, "Vehicles", "easy").get(0);
    }

    public Map<String, Map<String, Long>> getQuestionCountsByCategoryAndDifficulty() {
        List<Object[]> results = questionRepository.countQuestionsByCategoryAndDifficulty();
        Map<String, Map<String, Long>> counts = new HashMap<>();

        for (Object[] row : results) {
            String category = (String) row[0];
            String difficulty = (String) row[1];
            Long count = (Long) row[2];

            counts.computeIfAbsent(category, k -> new HashMap<>())
                  .put(difficulty, count);
        }

        return counts;
    }

    public Optional<Question> findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}
