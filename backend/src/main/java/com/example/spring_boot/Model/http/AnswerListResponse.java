package com.example.spring_boot.Model.http;

import java.util.List;

public class AnswerListResponse {
    private List<Boolean> corrects;
    private List<String> correctAnswers;

    public void setCorrects(List<Boolean> corrects){this.corrects=corrects;}
    public List<Boolean> getCorrects(){return this.corrects;}

    public void setCorrectAnswers(List<String> correctAnswers){this.correctAnswers = correctAnswers;}
    public List<String> getCorrectAnswers(){return this.correctAnswers;}

}
