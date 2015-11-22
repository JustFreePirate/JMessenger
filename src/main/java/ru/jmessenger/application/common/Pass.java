package ru.jmessenger.application.common;

import java.io.Serializable;

/**
 * Created by dima on 17.11.15.
 */
public class Pass implements Serializable {
    public Pass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    String pass;
}