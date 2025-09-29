package com.example.spring_boot.Model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class GameState{

    private List<Question> questions = new CopyOnWriteArrayList<>();
    private int questionIdx = 0;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;


    public List<Question> getQuestions(){return this.questions;}
    public void setQuestions(List<Question> questions){this.questions = questions;}

    public void setQuestionIdx(int idx){questionIdx=idx;}
    public int getQuestionIdx(){return this.questionIdx;}

    public void setGameStatus(GameStatus status){this.gameStatus = status;}
    public GameStatus getGameStatus(){return this.gameStatus;}

    


}