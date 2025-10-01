package com.example.spring_boot.Model.http;


public class AnswerRequest {
    private String roomName;
    private String username;
    private Long questionId;
    private String selectedAnswer; // index of the chosen answer

    public AnswerRequest() {}

    public AnswerRequest(String roomName, String username, Long questionId, String selectedAnswer) {
        this.roomName = roomName;
        this.username = username;
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
    }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username;}

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer;}
}
