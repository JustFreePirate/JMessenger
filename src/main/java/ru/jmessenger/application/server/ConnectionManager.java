package ru.jmessenger.application.server;

import ru.jmessenger.application.common.*;
import ru.jmessenger.application.common.Package;
import sun.rmi.runtime.Log;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.security.sasl.AuthenticationException;
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
    private static final int TIMEOUT = 500;
    public static final int BUFF_LEN = 1024*5;
    private HashMap<Login, Connection> connections;
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

    synchronized private void addConnectionToMap(Login login, Connection connection) {
        Connection previous = connections.put(login, connection);
        if (previous != null) {
            System.out.println("Someone connected from two machines");
        }
    }

    //send pack and return type of response
    private PackageType sendPackage(Login to, Package pack) {
        Connection connection = connections.get(to);
        if (connection != null) {
            try {
                connection.sendPackage(pack);
                return PackageType.RESP_MESSAGE_DELIVERED;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                //TODO push to mess query
                return PackageType.RESP_MESSAGE_IN_QUEUE;
            }
        } else {
            return PackageType.RESP_MESSAGE_USER_NOT_FOUND;
        }
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
        private Login login;
        PackageService packageService;

        Connection(Socket socket) {
            System.out.println("someone connected");
            System.out.printf("Connection to %s:%s is open\n", socket.getInetAddress(), socket.getPort());
            this.socket = socket;
            packageService = new PackageService();
            start(); //соединение в своем потоке
        }

        @Override
        public void run() {
            listenSocket();
        }

        private void listenSocket() {
            try {
                socket.setSoTimeout(TIMEOUT); //ждем ответа TIMEOUT миллисек
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                byte[] buf = new byte[BUFF_LEN];
                int r = 0;
                String request;
                while (true) {
                    try {
                        if ((r = is.read(buf)) > 0) {
                            //получаем новый пакет
                            try {
                                Package receivedPack = Package.deserialize(buf);
                                packageService.processPackage(receivedPack);
                            } catch (Exception e) {
                                System.out.println("failed to deserialize");
                            }
                        } else {
                            //клиент отключился
                            closeConnection();
                            return;
                        }
                    } catch (SocketTimeoutException e) {
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                closeConnection();
            }
        }

        boolean isAuthorized() {
            return login == null;
        }

        synchronized void sendPackage(Package aPackage) throws IOException {
            byte[] serialized = aPackage.serialize();
            OutputStream os = socket.getOutputStream();
            os.write(serialized, 0, serialized.length);
            os.flush();
        }

        void sendResponse(PackageType type) {
            try {
                sendPackage(new Package(type));
            } catch (IOException e) {
                System.out.println("Failed to send response");
            }
        }

        void sendResponse(Login[] searchAnswer) {
            try {
                sendPackage(new Package(searchAnswer));
            } catch (IOException e) {
                System.out.println("Failed to send response");
            }
        }


        void closeConnection() {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("failed to close connection");
            }
            //delete from connections
            System.out.printf("Connection to %s:%s was closed\n", socket.getInetAddress(), socket.getPort());
        }


        class PackageService {
            Package currentPackage;

            PackageService() {
            }

            //обрабатывает пришедший пакет
            void processPackage(Package pack) {
                currentPackage = pack;
                try {
                    if (!isAuthorized()) {
                        doAuth();
                    } else {
                        PackageType packType = pack.getType();
                        switch (packType) {
                            case REQ_SEND_MESSAGE:
                            case REQ_SEND_FILE:
                                Login from = login; //current connection login
                                Login to = pack.getLogin();
                                pack.setLogin(from);
                                PackageType responseType = ConnectionManager.this.sendPackage(to, pack);
                                sendResponse(responseType);
                                break;

                            case REQ_SEARCH:
                                //TODO search request
                                Login[] searchAnswer = null; //searchForLogin(pack.getLogin());
                                sendResponse(searchAnswer);
                                break;

                            default:
                                System.out.println("Bad type of request");
                        }

                    }
                } catch (AuthenticationException e) {
                    System.out.println(e.getMessage());
                }
            }

            void doAuth() throws AuthenticationException {
                if (currentPackage.getType() == PackageType.REQ_AUTH) {
                    Login packLogin = currentPackage.getLogin();
                    Pass packPass = currentPackage.getPass();
                    //check pass and login
                    boolean match = true; //isMatch(packLogin, packPass); TODO запрос к базе данных
                    if (match) {
                        login = packLogin; //set connection login
                        addConnectionToMap(packLogin, Connection.this);
                        sendResponse(PackageType.RESP_AUTH_OK);
                        //TODO send messages from query
                    } else {
                        sendResponse(PackageType.RESP_AUTH_FAILED);
                    }
                } else {
                    throw new AuthenticationException("Expected AUTH REQUEST");
                }
            }

        }
    }
}
