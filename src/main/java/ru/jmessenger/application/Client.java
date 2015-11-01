package ru.jmessenger.application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Дмитрий on 19.10.2015.
 * Поведение приложения как клиента сети
 * Singleton, т.к. приложение - это один клиент.
 */
public final class Client {
    private final List<Dialog> dialogList = new LinkedList<>();
    private static final Client OUR_INSTANCE = new Client();

    public static Client getInstance() {
        return OUR_INSTANCE;
    }

    private Client() {
        //получение данных о себе из бд или с сервера

    }

    public void addDialog(final Dialog dialog) {
        dialogList.add(dialog);
    }

    public void delDialog(final Dialog dialog) {
        dialogList.remove(dialog);
    }

}
