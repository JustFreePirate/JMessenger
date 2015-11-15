package ru.jmessenger.application;

import java.util.Date;

/**
 * Created by Сергей on 15.11.2015.
 */
public class TemproraryClassMessage {
    private final Long sender_id;
    private final Long recipient_id;
    private final String date;
    private final String message;

    TemproraryClassMessage(Long s_id, Long r_id, String date, String message){
        this.sender_id = s_id;
        this.recipient_id = r_id;
        this.date = date;
        this.message = message;
    }

    public Long getSenderId() {
        return sender_id;
    }
    public Long getRecipientId() {
        return recipient_id;
    }
    public String getDueDate() {
        return date;
    }
    public String getMessage() {
        return message;
    }
}
