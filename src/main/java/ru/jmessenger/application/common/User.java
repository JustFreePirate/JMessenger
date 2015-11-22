package ru.jmessenger.application.common;

/**
 * Created by Сергей on 21.11.15.
 */
public class User {
    private final Login login;
    private final Pass pass;

    public User(String login, String pass) {
        this.login = new Login(login);
        this.pass = new Pass(pass);
    }
    public User(Login login, Pass pass) {
        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return login.toString();
    }
    public String getHashPass() {
        return String.valueOf(pass.getPass().hashCode());
    }
}
