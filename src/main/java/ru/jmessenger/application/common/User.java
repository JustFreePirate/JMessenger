package ru.jmessenger.application.common;

/**
 * Created by dima on 17.11.15.
 */
public class User {
    private final String login;
    private final String hashPass;

    User(String login, String hashPass) {
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
