package com.example.spring_boot.Model.http;

public class AnswerResponse {
    private boolean correct;
    private String correctAnswer;

    public AnswerResponse(){}
    public AnswerResponse(boolean correct, String correctAnswer){
        this.correct = correct;
        this.correctAnswer = correctAnswer;
    }

    public void setCorrect(boolean correct){this.correct=correct;}
    public boolean getCorrect(){return this.correct;}

    public void setCorrectAnswer(String correctAnswer){this.correctAnswer=correctAnswer;}
    public String getCorrectAnswer(){return this.correctAnswer;}
    
}
