package ru.jmessenger.application.server;

import ru.jmessenger.application.common.*;
import ru.jmessenger.application.common.Package;
import ru.jmessenger.application.db.DatabaseManager;

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
import java.util.List;


/**
 * Created by Дмитрий on 19.10.2015.
 * Сервер ждет новых клиентов и устанавливает с ними соединения
 */
public class ConnectionManager extends Thread {
    private final int PORT;
    private static final int TIMEOUT = 500;
    public static final int BUFF_LEN = 1024 * 5;
    private HashMap<Login, Connection> connections;
    private ServerSocketFactory serverSocketFactory;
    //private static String ksName = "src/res/server_key_store.jks";
    //private static char[] crtPass = "free240195".toCharArray();
    private static DatabaseManager databaseManager;


    ConnectionManager(int port) {
        this.PORT = port;
        this.databaseManager = new DatabaseManager();
        connections = new HashMap<>();
        try {
            serverSocketFactory = getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("failed to create socket factory");
        }
        setPriority(NORM_PRIORITY);
    }

    private ServerSocketFactory getSocketFactory() throws Exception {
        return ServerSocketFactory.getDefault();
    }

    synchronized private void addConnectionToMap(Login login, Connection connection) {
        Connection previous = connections.put(login, connection);
        if (previous != null) {
            System.out.println("Someone connected from two machines");
        }
    }

    synchronized private void delConnectionFromMap(Login login) {
        connections.remove(login);
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
                databaseManager.addPackage(to, pack);
                return PackageType.RESP_MESSAGE_IN_QUEUE;
            }
        } else {
            boolean exist;
            try {
                exist = databaseManager.isUserExists(to);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return PackageType.RESP_SERVER_ERROR;
            }
            if (exist) {
                //System.out.println(e.getMessage());
                databaseManager.addPackage(to, pack);
                return PackageType.RESP_MESSAGE_IN_QUEUE;
            } else {
                return PackageType.RESP_MESSAGE_USER_NOT_FOUND;
            }
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
                            Package receivedPack;
                            try {
                                receivedPack = Package.deserialize(buf);
                            } catch (Exception e) {
                                System.out.println("failed to deserialize");
                                continue;
                            }
                            packageService.processPackage(receivedPack);
                        } else {
                            //клиент отключился
                            closeConnection();
                            return;
                        }
                    } catch (SocketTimeoutException e) {
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                closeConnection();
            }
        }

        boolean isAuthorized() {
            return login != null;
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
                if (isAuthorized()) {
                    delConnectionFromMap(login);
                }
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
                if (!isAuthorized()) {
                    doAuth();
                } else {
                    PackageType packType = pack.getType();
                    switch (packType) {
                        case REQ_SEND_MESSAGE:
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

                        case REQ_SIGN_OUT:
                            delConnectionFromMap(login);
                            login = null;
                            sendResponse(PackageType.RESP_SIGN_OUT_OK);
                            break;

                        default:
                            sendResponse(PackageType.RESP_SIGN_IN_FAILED);
                    }
                }

            }

            void doAuth() {
                Login packLogin = currentPackage.getLogin();
                Pass packPass = currentPackage.getPass();
                switch (currentPackage.getType()) {
                    case REQ_SIGN_IN:
                        //check pass and login
                        boolean match;
                        try {
                            match = databaseManager.isUserExists(new User(packLogin, packPass));
                        } catch (Exception e) {
                            e.printStackTrace(); //smth wrong with database
                            sendResponse(PackageType.RESP_SERVER_ERROR);
                            return;
                        }
                        if (match) {
                            login = packLogin; //set connection login
                            addConnectionToMap(packLogin, Connection.this);
                            sendResponse(PackageType.RESP_SIGN_IN_OK);

                            try {
                                List<Package> messageQueue = databaseManager.getPackageListForUser(packLogin);
                                for (Package entry : messageQueue) {
                                    sendPackage(entry);
                                }
                            } catch (Exception e) {
                                sendResponse(PackageType.RESP_SERVER_ERROR);
                            }
                        } else {
                            sendResponse(PackageType.RESP_SIGN_IN_FAILED);
                        }
                        break;

                    case REQ_SIGN_UP:
                        //login and pass filer (length >= 6, etc )
                        if (!isCorrectLogin(packLogin)) {
                            sendResponse(PackageType.RESP_SIGN_UP_LOGIN_FILTER_FAILED);
                            return;
                        }
                        if (!isCorrectPass(packPass)) {
                            sendResponse(PackageType.RESP_SIGN_UP_PASS_FILTER_FAILED);
                        }
                        boolean exist;
                        try {
                            exist = databaseManager.isUserExists(packLogin);
                        } catch (Exception e) {
                            sendResponse(PackageType.RESP_SERVER_ERROR);
                            return;
                        }
                        if (!exist) {
                            try {
                                databaseManager.addUser(new User(packLogin, packPass));
                                sendResponse(PackageType.RESP_SIGN_UP_OK);
                            } catch (Exception e) {
                                sendResponse(PackageType.RESP_SERVER_ERROR);
                            }
                        } else {
                            sendResponse(PackageType.RESP_SIGN_UP_USER_ALREADY_EXIST);
                        }
                        break;

                    default:
                        sendResponse(PackageType.RESP_SIGN_IN_FAILED);
                }
            }

            boolean isCorrectLogin(Login login) {
                return true; //TODO
            }

            boolean isCorrectPass(Pass pass) {
                return true; //TODO
            }
        }
    }
}
