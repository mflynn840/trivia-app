package com.example.spring_boot.Model;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PlayerDTO {
    
    private String sessionId;

    
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private byte[] profilePicture;
    private String username;
    private boolean isReady;
    private Long id;
    private Long score;

    public String getSessionId(){return this.sessionId;}
    public void setSessionId(String sessionId){this.sessionId = sessionId;}
    public byte[] getProfilePicture(){return this.profilePicture;}
    public void setProfilePicture(byte[] profilePicture){this.profilePicture = profilePicture;}

    public String getUsername(){return this.username;}
    public void setUsername(String username){this.username = username;}

    public boolean isReady(){return this.isReady;}
    public void setReady(boolean isReady){this.isReady = isReady;}

    public Long getScore(){return this.score;}
    public void setScore(Long score){this.score = score;}


    public PlayerDTO(Player p){
        this.sessionId = p.getSessionId();
        this.profilePicture = p.getProfilePicture();
        this.username = p.getUsername();
        this.isReady = p.isReady();
        this.id = p.getId();
        this.score = p.getScore();
        
    }
    
}
