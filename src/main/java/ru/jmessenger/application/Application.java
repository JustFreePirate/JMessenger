package ru.jmessenger.application;

/**
 * Created by Dima on 17.10.2015.
 */
public class Application {
    private static int PORT = 3128;

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.start();
    }
}
