package com.example.spring_boot.Model.coop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.spring_boot.Model.Question;


public class GameState{

    private Map<String, Integer> scores = new HashMap<String, Integer>(); //username -> score
    private List<Question> questions = new CopyOnWriteArrayList<>();
    private int questionIdx = 0;
    private GameStatus gameStatus = GameStatus.WAITING_FOR_PLAYERS;

    //Constructors
    public GameState(){}
    //Getters/setters
    public List<Question> getQuestions(){return this.questions;}
    public void setQuestions(List<Question> questions){this.questions = questions;}

    public void setQuestionIdx(int idx){questionIdx=idx;}
    public int getQuestionIdx(){return this.questionIdx;}

    public void setGameStatus(GameStatus status){this.gameStatus = status;}
    public GameStatus getGameStatus(){return this.gameStatus;}

    public void setScores(Map<String,Integer> scores){this.scores=scores;}
    public Map<String,Integer> getScores(){return this.scores;}

    public void addUser(String username){this.scores.put(username, 0);}
    
    public int getNumQuestions(){
        if(this.questions == null) return 0;
        else return this.questions.size();
    }

    public void incrementQuestionIdx(){this.questionIdx++;}


    /**
     * Modify the scores map to add/remove points for given user
     * @param username
     * @param numPoints
     */
    public void addPoints(String username, int numPoints){
        if(scores.containsKey(username)){

            scores.put(username, scores.get(username)+numPoints);
        }else{
            throw new IllegalArgumentException("user not found in scores");
        }
    }

    public Question getCurrentQuestion() {
        if (questions.isEmpty() || questionIdx >= questions.size()) return null;
        return questions.get(questionIdx);
    }
}