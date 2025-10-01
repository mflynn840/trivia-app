package com.example.spring_boot.Model.http;

public class TimerRequest {

    private String type;
    private Long questionId;
    private Long startEpochTime;
    private Long durationMs;


    //Constructors
    public TimerRequest(){}
    // Getters and Setters
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getStartEpochTime() {
        return startEpochTime;
    }
    public void setStartEpochTime(Long startEpochTime) {
        this.startEpochTime = startEpochTime;
    }

    public Long getDurationMs() {
        return durationMs;
    }
    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}
