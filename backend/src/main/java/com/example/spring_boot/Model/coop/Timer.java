package com.example.spring_boot.Model.coop;


public class Timer {

    private Long questionId;
    private Long startEpochTime;
    private Long durationMs;

    //Constructors
    public Timer(){}
    public Timer(Long questionId, Long durationMs){
        this.startEpochTime = System.currentTimeMillis();
        this.questionId = questionId;
        this.durationMs = durationMs;
        
    }
    // Getters and Setters
    public Long getQuestionId() {return questionId;}
    public void setQuestionId(Long questionId) { this.questionId = questionId;}

    public Long getStartEpochTime() { return startEpochTime;}
    public void setStartEpochTime(Long startEpochTime) {this.startEpochTime = startEpochTime;}

    public Long getDurationMs() { return durationMs;}
    public void setDurationMs(Long durationMs) {this.durationMs = durationMs;}
}
