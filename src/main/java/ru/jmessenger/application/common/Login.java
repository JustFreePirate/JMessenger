package ru.jmessenger.application.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by dima on 17.11.15.
 */
public class Login implements Serializable{
    String login;

    public Login(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Login) {
            return Objects.equals(login, ((Login) obj).login);
        }
        return false;
    }


}