package com.example.spring_boot.Model.user;

import jakarta.persistence.Embeddable;

@Embeddable
public class ColorPallete {

    private Long PRIMARY_BUTTON_COLOR = 0xFF00F0FFL;
    private Long SECONDARY_BUTTON_COLOR = 0xFF000000L;
    private Long PRIMARY_CARD_COLOR = 0xFFFFFFFFL;  
    private Long NEON_CARD_COLOR = 0xFF00FFFFL;    
    private Long CARD_TEXT_COLOR = 0xFFFFFFFFL;
    private Long QUESTION_PRIMARY_COLOR = 0x99FFFFFFL;
    private Long QUESTION_TEXT_COLOR = 0xFF000000L;
    private Long SUBMIT_BUTTON_PRIMARY_COLOR = 0xFF009688L;
    private Long SUBMIT_BUTTON_TEXT_COLOR = 0xFFFFFFFFL;

    public Long getPRIMARY_BUTTON_COLOR() { return PRIMARY_BUTTON_COLOR; }
    public void setPRIMARY_BUTTON_COLOR(Long PRIMARY_BUTTON_COLOR) { this.PRIMARY_BUTTON_COLOR = PRIMARY_BUTTON_COLOR; }

    public Long getSECONDARY_BUTTON_COLOR() { return SECONDARY_BUTTON_COLOR; }
    public void setSECONDARY_BUTTON_COLOR(Long SECONDARY_BUTTON_COLOR) { this.SECONDARY_BUTTON_COLOR = SECONDARY_BUTTON_COLOR; }

    public Long getPRIMARY_CARD_COLOR() { return PRIMARY_CARD_COLOR; }
    public void setPRIMARY_CARD_COLOR(Long PRIMARY_CARD_COLOR) { this.PRIMARY_CARD_COLOR = PRIMARY_CARD_COLOR; }

    public Long getNEON_CARD_COLOR() { return NEON_CARD_COLOR; }
    public void setNEON_CARD_COLOR(Long NEON_CARD_COLOR) { this.NEON_CARD_COLOR = NEON_CARD_COLOR; }

    public Long getCARD_TEXT_COLOR() { return CARD_TEXT_COLOR; }
    public void setCARD_TEXT_COLOR(Long CARD_TEXT_COLOR) { this.CARD_TEXT_COLOR = CARD_TEXT_COLOR; }

    public Long getQUESTION_PRIMARY_COLOR() { return QUESTION_PRIMARY_COLOR; }
    public void setQUESTION_PRIMARY_COLOR(Long QUESTION_PRIMARY_COLOR) { this.QUESTION_PRIMARY_COLOR = QUESTION_PRIMARY_COLOR; }

    public Long getQUESTION_TEXT_COLOR() { return QUESTION_TEXT_COLOR; }
    public void setQUESTION_TEXT_COLOR(Long QUESTION_TEXT_COLOR) { this.QUESTION_TEXT_COLOR = QUESTION_TEXT_COLOR; }

    public Long getSUBMIT_BUTTON_PRIMARY_COLOR() { return SUBMIT_BUTTON_PRIMARY_COLOR; }
    public void setSUBMIT_BUTTON_PRIMARY_COLOR(Long SUBMIT_BUTTON_PRIMARY_COLOR) { this.SUBMIT_BUTTON_PRIMARY_COLOR = SUBMIT_BUTTON_PRIMARY_COLOR; }

    public Long getSUBMIT_BUTTON_TEXT_COLOR() { return SUBMIT_BUTTON_TEXT_COLOR; }
    public void setSUBMIT_BUTTON_TEXT_COLOR(Long SUBMIT_BUTTON_TEXT_COLOR) { this.SUBMIT_BUTTON_TEXT_COLOR = SUBMIT_BUTTON_TEXT_COLOR; }

    @Override
    public String toString() {
        return "ColorPallete{" +
                "PRIMARY_BUTTON_COLOR=0x" + Long.toHexString(PRIMARY_BUTTON_COLOR) +
                ", SECONDARY_BUTTON_COLOR=0x" + Long.toHexString(SECONDARY_BUTTON_COLOR) +
                ", PRIMARY_CARD_COLOR=0x" + Long.toHexString(PRIMARY_CARD_COLOR) +
                ", NEON_CARD_COLOR=0x" + Long.toHexString(NEON_CARD_COLOR) +
                ", CARD_TEXT_COLOR=0x" + Long.toHexString(CARD_TEXT_COLOR) +
                ", QUESTION_PRIMARY_COLOR=0x" + Long.toHexString(QUESTION_PRIMARY_COLOR) +
                ", QUESTION_TEXT_COLOR=0x" + Long.toHexString(QUESTION_TEXT_COLOR) +
                ", SUBMIT_BUTTON_PRIMARY_COLOR=0x" + Long.toHexString(SUBMIT_BUTTON_PRIMARY_COLOR) +
                ", SUBMIT_BUTTON_TEXT_COLOR=0x" + Long.toHexString(SUBMIT_BUTTON_TEXT_COLOR) +
                '}';
    }

}
