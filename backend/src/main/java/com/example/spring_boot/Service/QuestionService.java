package com.example.spring_boot.Service;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Model.http.AnswerListResponse;
import com.example.spring_boot.Model.http.AnswerRequest;
import com.example.spring_boot.Model.http.AnswerResponse;
import com.example.spring_boot.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    public List<Question> getRandomQuestions(int count, String category, String difficulty) {
        List<Question> questions = questionRepository.findRandomQuestions(count, category, difficulty);
        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions available with the requested parametrs");
        }
        return questions;
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
        long totalAll = 0L;
        
        for (Object[] row : results) {
            String category = (String) row[0];
            String difficulty = (String) row[1];
            Number countNum = (Number) row[2];
            Long count = countNum.longValue();

            // Normal category/difficulty
            counts.computeIfAbsent(category, k -> new HashMap<>())
                .put(difficulty, count);

            // Mixed category (aggregating difficulties)
            counts.computeIfAbsent("Mixed", k -> new HashMap<>())
                .merge(difficulty, count, Long::sum);

            // Mixed difficulty (aggregating categories)
            counts.computeIfAbsent(category, k -> new HashMap<>())
                .merge("Mixed", count, Long::sum);
            
            // Track grand total
            totalAll += count;
        }
        // Mixed / Mixed = everything
        counts.computeIfAbsent("Mixed", k -> new HashMap<>())
            .put("Mixed", totalAll);


        return counts;
    }
    

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    //checking answers
    public AnswerResponse checkAnswer(AnswerRequest answerRequest) {
        Question question = questionRepository.findById(answerRequest.getQuestionId())
                            .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        
        boolean isCorrect = question.getCorrectAnswer().equals(answerRequest.getSelectedAnswer());

        AnswerResponse response = new AnswerResponse(isCorrect, question.getCorrectAnswer());
        return response;
            
    }

    public AnswerListResponse checkAnswers(List<AnswerRequest> request) {
        
        ArrayList<String> correctAnswers = new ArrayList<>();
        ArrayList<Boolean> corrects = new ArrayList<>();
        
        for(AnswerRequest item: request){
            AnswerResponse itemA = checkAnswer(item);
            corrects.add(itemA.getCorrect());
            correctAnswers.add(itemA.getCorrectAnswer());
        }

        AnswerListResponse response = new AnswerListResponse();
        response.setCorrectAnswers(correctAnswers);
        response.setCorrects(corrects);
        return response;
    }


}
