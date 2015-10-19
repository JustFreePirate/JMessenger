package ru.jmessenger.application;

import ru.jmessenger.application.listeners.SimpleListener;

import java.net.ServerSocket;

/**
 * Created by Дмитрий on 19.10.2015.
 * Сервер ждет новых клиентов и устанавливает с ними диалоги
 */
public class Server extends Thread {
    private final int port;

    Server(int port) {
        this.port = port;
        setPriority(NORM_PRIORITY);
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Server is started");
            while (true) {
                try {
                    Client.getInstance().addDialog(new Dialog(ss.accept(), SimpleListener.getInstance()));
                } catch (Exception e) {
                    System.out.println("Failed open new Dialog: " + e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Failed open ServerSocket: " + e);
        }
    }
}
