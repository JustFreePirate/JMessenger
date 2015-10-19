package ru.jmessenger.application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий on 19.10.2015.
 * Поведение приложения как клиента сети
 * Singleton, т.к. приложение - это один клиент.
 */
public class Client {
    private final List<Dialog> dialogList = new LinkedList<>();
    private final static Client ourInstance = new Client();

    public static Client getInstance() {
        return ourInstance;
    }

    private Client() {
        //получение данных о себе из бд или с сервера

    }

    public void addDialog(Dialog dialog) {
        dialogList.add(dialog);
    }

    public void delDialog(Dialog dialog) {
        dialogList.remove(dialog);
    }

}
