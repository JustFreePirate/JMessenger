package ru.jmessenger.application.common;

import java.util.Date;

/**
 * Created by dima on 17.11.15.
 */
public class Package {
    private final String senderLogin;
    private final String recipientLogin;
    private final String date;
    private final String message;
    private final byte[] file;


    public Package(String message, String senderLogin, String recipientLogin) {
        this(message, senderLogin, recipientLogin, new Date().toString(), null);
    }

    public Package(String message, String senderLogin, String recipientLogin, String date, byte[] file) {
        this.senderLogin = senderLogin;
        this.recipientLogin = recipientLogin;
        this.date = date;
        this.message = message;
        this.file = file;
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
    public byte[] getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "from: " + senderLogin + "\n" +
                "to: " + recipientLogin + "\n"
                + message + "\n"
                + "date: " + date + "\n";
    }
}
