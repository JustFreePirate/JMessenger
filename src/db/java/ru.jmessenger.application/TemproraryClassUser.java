package ru.jmessenger.application;

import java.util.Date;

/**
 * Created by Сергей on 15.11.2015.
 */
public class TemproraryClassUser {
    private final String login;
    private final String hash_pass;

    TemproraryClassUser(String login, String hash_pass){
        this.login = login;
        this.hash_pass = hash_pass;
    }

    public String getLogin() {
        return login;
    }
    public String getHashPass() {
        return hash_pass;
    }

}


