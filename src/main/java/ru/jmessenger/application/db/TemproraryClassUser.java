package ru.jmessenger.application.db;

import java.util.Date;

/**
 * Created by Сергей on 15.11.2015.
 * del
 */
public class TemproraryClassUser {
    private final String login;
    private final String hashPass;

    TemproraryClassUser(String login, String hashPass){
        this.login = login;
        this.hashPass = hashPass;
    }

    public String getLogin() {
        return login;
    }
    public String getHashPass() {
        return hashPass;
    }
}


