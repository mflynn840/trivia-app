package com.example.spring_boot.Model;

import java.util.List;

enum GameStatus {
    WAITING,
    IN_PROGRESS,
    FINISHED,
    WAITING_FOR_PLAYERS
}


public class GameState{

    private List<Question> questions;
    private int questionIdx;


    public List<Question> getQuestions(){return this.questions;}
    public void setQuestions(List<Question> questions){this.questions = questions;}

    public void setQuestionIdx(int idx){questionIdx=idx;}
    public int getQuestionIdx(){return this.questionIdx;}

    


}