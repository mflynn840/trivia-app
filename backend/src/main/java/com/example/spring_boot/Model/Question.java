package com.example.spring_boot.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers;
    private String category;
    private String difficulty;
    private String type;

    // Default constructor
    public Question() {}

    // Constructor with parameters
    public Question(String question, String correctAnswer, List<String> incorrectAnswers, 
                   String category, String difficulty, String type) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return this.incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Override toString() for better debugging
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", incorrectAnswers=" + incorrectAnswers +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
