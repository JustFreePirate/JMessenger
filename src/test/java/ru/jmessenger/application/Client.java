package ru.jmessenger.application;

import java.io.*;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Scanner;

import ru.jmessenger.application.common.*;
import ru.jmessenger.application.common.Package;

import javax.net.ssl.*;

/**
 * Created by Сергей on 20.11.2015.
 */
public class Client {
    private static String tsName = "src/res/client_key_store.jks";
    private static String ServerCrtName = "src/res/server.crt";
    private static final int TIMEOUT = 500;
    private static final int BUFF_LEN = 1024 * 5;
    private static final int PORT = 3128;
    InputStream is;
    OutputStream os;

    private PackageService packageService;
    private SSLSocketFactory socketFactory;
    private SSLSocket sslSocket;

    public Client() throws Exception {
        socketFactory = getSocketFactory();
        sslSocket = (SSLSocket) socketFactory.createSocket("localhost", PORT);
        packageService = new PackageService();
        sslSocket.setSoTimeout(TIMEOUT); //ждем ответа TIMEOUT миллисек
        is = sslSocket.getInputStream();
        os = sslSocket.getOutputStream();
    }

    public void start() throws Exception {

        Thread t = new Thread(new Runnable() {
            public void run() {
                listen();
            }
        });
        t.start();

        Scanner scanner = new Scanner(System.in);
        String str;
        while (true) {
            str = scanner.nextLine();

            Package aPackage;
            try {
                aPackage = stringToPackage(str);
            } catch (Throwable e) {
                System.out.println("Incorrect command");
                continue;
            }

            sendPackage(aPackage);
        }
    }

    private Package stringToPackage(String str) throws Exception {
        //Что тут происходит:
        //Пользователь набирает команду, она парсится следующим образом:
        //
        //PackageType.REQ_SIGN_IN;
        // Auth; Bob; 1234567890;
        //
        //Пусть FILE = MESSAGE
        //PackageType.REQ_SEND_MESSAGE;
        //PackageType.REQ_SEND_FILE;
        //Send; Alice; Hello!;
        //
        //Пока что не будем
        //PackageType.REQ_SEARCH;

        Scanner scanner = new Scanner(str).useDelimiter("; ");

        String command = scanner.next();
        String login = scanner.next();
        String passOrMessage = scanner.next();


        switch (command) {
            case "sign in":
                return new Package(PackageType.REQ_SIGN_IN, new Login(login), new Pass(passOrMessage));
            case "sign up":
                return new Package(PackageType.REQ_SIGN_UP, new Login(login), new Pass(passOrMessage));
            case "sign out":
                return new Package(PackageType.REQ_SIGN_OUT);
            case "send":
                return new Package(passOrMessage, new Login(login));
            default:
                throw new Exception();
        }
    }

    private static SSLSocketFactory getSocketFactory() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        try {
            FileInputStream trustStream = new FileInputStream(tsName);
            ks.load(trustStream, null);
        } catch (FileNotFoundException e) {
            CertificateFactory crtFactory = CertificateFactory.getInstance("X.509");
            Certificate cert = crtFactory.generateCertificate(new FileInputStream(ServerCrtName));
            ks.load(null, null);
            ks.setCertificateEntry("server_cert", cert);
            FileOutputStream fos = new FileOutputStream(tsName);
            ks.store(fos, "123456".toCharArray());
            fos.close();
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        sslContext.init(null, trustManagers, null);
        return sslContext.getSocketFactory();
    }

    private void sendPackage(Package aPackage) throws IOException {
        byte[] serialized = aPackage.serialize();
        System.out.println("length of package: " + serialized.length);
        OutputStream outputstream = sslSocket.getOutputStream();
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);

        outputstream.write(serialized, 0, serialized.length);
        outputstream.flush();
    }

    private void listen() {
        try {

            byte[] buf = new byte[BUFF_LEN];
            int r = 0;
            String request;

            while (true) {
                try {
                    if ((r = is.read(buf)) > 0) {
                        try {
                            Package receivedPack = Package.deserialize(buf);
                            packageService.processPackage(receivedPack);
                        } catch (Exception e) {
                            System.out.println("failed to deserialize");
                        }
                    }
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PackageService {
        Package currentPackage;
        SimpleDateFormat formatter;

        PackageService() {
            formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        }

        //обрабатывает пришедший пакет
        void processPackage(Package pack) {
            currentPackage = pack;

            PackageType packType = pack.getType();
            switch (packType) {
                case REQ_SEND_MESSAGE:
                    System.out.println("From: " + pack.getLogin() + " " + formatter.format(pack.getDate()) +
                            "\n" + "-> " + pack.getMessage());
                    break;

                default:
                    System.out.println(packType.name());
            }
        }
    }


    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}