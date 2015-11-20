package ru.jmessenger.application.common;

/**
 * Created by dima on 17.11.15.
 */
public class Login {
    Login(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    String login;
}