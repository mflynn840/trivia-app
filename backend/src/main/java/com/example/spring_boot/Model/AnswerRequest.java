package com.example.spring_boot.Model;


public class AnswerRequest {
    private Long roomId;
    private Long playerId;
    private Long questionId;
    private int selectedOption; // index of the chosen answer

    public AnswerRequest() {}

    public AnswerRequest(Long roomId, Long playerId, Long questionId, int selectedOption) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
    }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public int getSelectedOption() { return selectedOption; }
    public void setSelectedOption(int selectedOption) { this.selectedOption = selectedOption; }
}
