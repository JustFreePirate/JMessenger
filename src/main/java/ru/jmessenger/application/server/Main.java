package ru.jmessenger.application.server;

/**
 * Created by Dima on 17.10.2015.
 */
public class Main {
    private static int PORT = 3128;

    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager(PORT);
        connectionManager.start();
    }
}
