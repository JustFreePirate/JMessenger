package ru.jmessenger.application.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Created by Дмитрий on 19.10.2015.
 * Сервер ждет новых клиентов и устанавливает с ними соединения
 */
public class ConnectionManager extends Thread {
    private final int PORT;
    private final int TIMEOUT = 500;
    private final int BUFF_LEN = 100;
    private HashMap<String, Connection> connections;


    ConnectionManager(int port) {
        this.PORT = port;
        connections = new HashMap<>();
        setPriority(NORM_PRIORITY);
    }

    //Ищем входящие соединения и создаем для них Connection
    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(PORT);
            System.out.println("ConnectionManager is started");
            while (true) {
                try {
                    new Connection(ss.accept());
                } catch (Exception e) {
                    System.out.println("Failed open new connection: " + e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Failed open ServerSocket: " + e);
        }
    }


    class Connection extends Thread {
        private final Socket socket;
        private String login;

        public Connection(Socket socket) {
            System.out.println("someone connected");
            System.out.printf("Connection to %s:%s is open\n", socket.getInetAddress(), socket.getPort());
            this.socket = socket;
            start(); //соединение в своем потоке
        }

        @Override
        public void run() {
            listenSocket();
        }

        private void listenSocket() {
            try {
                socket.setSoTimeout(TIMEOUT); //ждем ответа TIMEOUT миллисек
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();

                byte[] buf = new byte[BUFF_LEN];
                int r = 0;
                String request;
                while (true) {
                    try {
                        if ((r = is.read(buf)) > 0) {
                            //получили от пользователя строку request
                            request = new String(buf, 0, r);

                            //обрабатываем новую строку
                            System.out.println(request);
                            os.write(buf, 0, r);
                        } else {
                            //клиент отключился
                            closeConnection();
                            return;
                        }
                    } catch (SocketTimeoutException e) {
                    }
                }
            } catch (Exception e) {
                closeConnection();
            }
        }

        public void closeConnection() {
            try {
                //System.out.println("Closing connection to " + socket.getInetAddress() + ":" + socket.getPort());
                socket.close();
            } catch (IOException e) {
                //System.out.println("failed to close connection");
            }
            //delete from connections
            System.out.printf("Connection to %s:%s was closed\n", socket.getInetAddress(), socket.getPort());
        }
    }
}
