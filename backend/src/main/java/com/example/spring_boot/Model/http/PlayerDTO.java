package com.example.spring_boot.Model.http;

import com.example.spring_boot.Model.user.ColorPallete;
import com.example.spring_boot.Model.user.Player;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerDTO {

    
    //@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    //private byte[] profilePicture;
    private String username;

    @JsonProperty("isReady")
    private boolean isReady;
    private Long id;
    private Long score;
    private ColorPallete colorPallete;

    //public String getSessionId(){return this.sessionId;}
    //public void setSessionId(String sessionId){this.sessionId = sessionId;}
    //public byte[] getProfilePicture(){return this.profilePicture;}
    //public void setProfilePicture(byte[] profilePicture){this.profilePicture = profilePicture;}

    public String getUsername(){return this.username;}
    public void setUsername(String username){this.username = username;}

    public boolean isReady(){return this.isReady;}
    public void setReady(boolean isReady){this.isReady = isReady;}

    public Long getScore(){return this.score;}
    public void setScore(Long score){this.score = score;}

    public ColorPallete getColorPallete(){return colorPallete;}
    public void setColorPallete(ColorPallete colorPallete){this.colorPallete=colorPallete;}
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public PlayerDTO(){
        
    }

    public PlayerDTO(Player p){
        //this.profilePicture = p.getProfilePicture();
        this.username = p.getUsername();
        this.isReady = p.isReady();
        this.id = p.getId();
        this.score = p.getScore();
        this.colorPallete = p.getColorPallete();
        
    }
    
}
