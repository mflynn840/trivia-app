package com.example.spring_boot.Service;

import org.springframework.stereotype.Service;

import com.example.spring_boot.Model.Question;
import com.example.spring_boot.Repository.QuestionRepository;


@Service
public class QuestionService {

    private QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }


    public Question getQuestion(int index){
        if(index > this.questionRepository.count()){
            throw new IllegalArgumentException("index out of bounds");
        }
        return this.questionRepository.findByRowNumber(index);
    }
    
}
