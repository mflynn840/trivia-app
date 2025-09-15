package com.example.spring_boot.Service;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Question getQuestion(int index){
        if(index > this.questionRepository.count() || index < 1){
            throw new IllegalArgumentException("index out of bounds");
        }
        return this.questionRepository.findByRowNumber(index - 1); // Convert to 0-based
    }
    
    public List<Question> getRandomQuestions(int count) {
        long totalQuestions = this.questionRepository.count();
        if (totalQuestions == 0) {
            throw new IllegalArgumentException("No questions available");
        }
        
        // Use a Set to avoid duplicates
        java.util.Set<Integer> usedIndices = new java.util.HashSet<>();
        List<Question> questions = new java.util.ArrayList<>();
        
        int actualCount = Math.min(count, (int) totalQuestions);
        
        while (questions.size() < actualCount && usedIndices.size() < totalQuestions) {
            int randomIndex = random.nextInt((int) totalQuestions); // 0-based indexing
            
            if (!usedIndices.contains(randomIndex)) {
                usedIndices.add(randomIndex);
                try {
                    Question question = this.questionRepository.findByRowNumber(randomIndex);
                    if (question != null) {
                        questions.add(question);
                    }
                } catch (Exception e) {
                    // Skip this question if there's an issue
                    continue;
                }
            }
        }
        
        return questions;
    }

    public Question getRandomQuestion(String category, String difficulty) {
        // TODO: implement properly with repository queries
        // For now just return any random question
        long count = questionRepository.count();
        if (count == 0) return null;

        int idx = ThreadLocalRandom.current().nextInt((int) count);
        return questionRepository.findAll(PageRequest.of(idx, 1))
                                 .stream()
                                 .findFirst()
                                 .orElse(null);
    }

    public Question getRandomQuestion(String category, Object unused) {
        return getRandomQuestion(category, null);
    }

    public boolean checkAnswer(Long questionId, String answer) {
        return questionRepository.findById(questionId)
                .map(q -> q.getCorrectAnswer().equalsIgnoreCase(answer))
                .orElse(false);
    }
    
    public long getQuestionCount() {
        return this.questionRepository.count();
    }

    public Optional<Question> findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}
