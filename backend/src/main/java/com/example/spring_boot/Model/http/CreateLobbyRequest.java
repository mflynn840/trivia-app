// com.example.spring_boot.DTO.CreateLobbyRequest.java

package com.example.spring_boot.Model.http;

public class CreateLobbyRequest {
    private String name;
    private String category;
    private String difficulty;
    private int numQuestions;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getCategory(){return category;}
    public void setCategory(String category){this.category=category;}

    public String getDifficulty(){return difficulty;}
    public void setDifficulty(String difficulty){this.difficulty=difficulty;}

    public int getNumQuestions(){return numQuestions;}
    public void setNumQuestions(int numQuestions){this.numQuestions=numQuestions;}

}
