package com.example.spring_boot.Model.user;

import jakarta.persistence.Embeddable;

@Embeddable
public class ColorPallete {

    private int primaryButtonColor = 0xFF00F0FF;
    private int secondaryButtonColor = 0xFF000000;
    private int primaryCardColor = 0xFFFFFFFF;
    private int neonCardColor = 0xFF00FFFF;
    private int cardTextColor = 0xFFFFFFFF;
    private int questionPrimaryColor = 0x99FFFFFF;
    private int questionTextColor = 0xFF000000;
    private int submitButtonPrimaryColor = 0xFF009688;
    private int submitButtonTextColor = 0xFFFFFFFF;

    // Getters and setters
    public int getPrimaryButtonColor() { return primaryButtonColor; }
    public void setPrimaryButtonColor(int primaryButtonColor) { this.primaryButtonColor = primaryButtonColor; }

    public int getSecondaryButtonColor() { return secondaryButtonColor; }
    public void setSecondaryButtonColor(int secondaryButtonColor) { this.secondaryButtonColor = secondaryButtonColor; }

    public int getPrimaryCardColor() { return primaryCardColor; }
    public void setPrimaryCardColor(int primaryCardColor) { this.primaryCardColor = primaryCardColor; }

    public int getNeonCardColor() { return neonCardColor; }
    public void setNeonCardColor(int neonCardColor) { this.neonCardColor = neonCardColor; }

    public int getCardTextColor() { return cardTextColor; }
    public void setCardTextColor(int cardTextColor) { this.cardTextColor = cardTextColor; }

    public int getQuestionPrimaryColor() { return questionPrimaryColor; }
    public void setQuestionPrimaryColor(int questionPrimaryColor) { this.questionPrimaryColor = questionPrimaryColor; }

    public int getQuestionTextColor() { return questionTextColor; }
    public void setQuestionTextColor(int questionTextColor) { this.questionTextColor = questionTextColor; }

    public int getSubmitButtonPrimaryColor() { return submitButtonPrimaryColor; }
    public void setSubmitButtonPrimaryColor(int submitButtonPrimaryColor) { this.submitButtonPrimaryColor = submitButtonPrimaryColor; }

    public int getSubmitButtonTextColor() { return submitButtonTextColor; }
    public void setSubmitButtonTextColor(int submitButtonTextColor) { this.submitButtonTextColor = submitButtonTextColor; }

    // toString for logging
    @Override
    public String toString() {
        return "ColorPallete{" +
                "primaryButtonColor=0x" + Integer.toHexString(primaryButtonColor) +
                ", secondaryButtonColor=0x" + Integer.toHexString(secondaryButtonColor) +
                ", primaryCardColor=0x" + Integer.toHexString(primaryCardColor) +
                ", neonCardColor=0x" + Integer.toHexString(neonCardColor) +
                ", cardTextColor=0x" + Integer.toHexString(cardTextColor) +
                ", questionPrimaryColor=0x" + Integer.toHexString(questionPrimaryColor) +
                ", questionTextColor=0x" + Integer.toHexString(questionTextColor) +
                ", submitButtonPrimaryColor=0x" + Integer.toHexString(submitButtonPrimaryColor) +
                ", submitButtonTextColor=0x" + Integer.toHexString(submitButtonTextColor) +
                '}';
    }
}
