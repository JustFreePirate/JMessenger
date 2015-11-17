package ru.jmessenger.application.server;

import ru.jmessenger.application.common.*;
import ru.jmessenger.application.common.Package;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
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
    private SSLServerSocketFactory serverSocketFactory;
    private static String ksName = "src/res/server_key_store.jks";
    private static char[] crtPass = "free240195".toCharArray();


    ConnectionManager(int port) {
        this.PORT = port;
        connections = new HashMap<>();
        try {
            serverSocketFactory = getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("failed to create socket factory");
        }
        setPriority(NORM_PRIORITY);
    }

    private SSLServerSocketFactory getSocketFactory() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(ksName), null);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, crtPass);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        return sslContext.getServerSocketFactory();
    }

    //Ищем входящие соединения и создаем для них Connection
    @Override
    public void run() {
        try {
            ServerSocket ss = serverSocketFactory.createServerSocket(PORT);//new ServerSocket(PORT);
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
                            //TODO
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

        synchronized public void sendPackage(Package aPackage) {

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
