package ru.jmessenger.application;

import java.io.*;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Scanner;
import java.net.*;

import ru.jmessenger.application.common.*;
import ru.jmessenger.application.common.Package;

import javax.net.ssl.*;

/**
 * Created by Сергей on 20.11.2015.
 */
public class Client {
    private static final int TIMEOUT = 500;
    private static final int PORT = 3128;

    private ArrayDeque<Package> arrayDeque;

    private Sender sender;
    private Listener listener;

    private Socket socket;

    public Client() throws Exception {

        socket = new Socket("localhost", PORT);
        socket.setSoTimeout(TIMEOUT); //ждем ответа TIMEOUT миллисек

        arrayDeque = new ArrayDeque<Package>();
        sender = new Sender(socket);

        //В отдельном потоке принимаем пакеты
        new Listener(socket, arrayDeque);
        new PackageService(arrayDeque);
    }

    public void start() throws Exception {

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

            //Отправляем пакет
            sender.sendPackage(aPackage);

//Для Миши
//            if(!arrayDeque.isEmpty()){
//                System.out.println(this.getPackage().getType().toString());
//            }
        }
    }

    public Package getPackage() {
        Package pack =  arrayDeque.getFirst();
        arrayDeque.removeFirst();
        return  pack;
    }

    private Package stringToPackage(String str) throws Exception {

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


    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
