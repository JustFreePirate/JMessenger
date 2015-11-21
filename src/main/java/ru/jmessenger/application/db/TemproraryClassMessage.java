package ru.jmessenger.application.db;

import java.util.Date;

/**
 * Created by Сергей on 15.11.2015.
 * del
 */
public class TemproraryClassMessage {
    private final String senderLogin;
    private final String recipientLogin;
    private final String date;
    private final String message;

    TemproraryClassMessage(String senderLogin, String recipientLogin, String date, String message){
        this.senderLogin = senderLogin;
        this.recipientLogin = recipientLogin;
        this.date = date;
        this.message = message;
    }

    public String getSenderLogin() {
        return senderLogin;
    }
    public String getRecipientLogin() {
        return recipientLogin;
    }
    public String getDate() {
        return date;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "from: " + senderLogin + "\n" +
                "to: " + recipientLogin + "\n"
                + message + "\n"
                + "date: " + date + "\n";
    }
}
