package ru.jmessenger.application.common;

/**
 * Created by dima on 17.11.15.
 */
public class Login {
    Login(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    String login;
}