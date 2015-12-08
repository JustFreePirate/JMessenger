package com.example.julia.uley;

import com.example.julia.uley.common.Package;

import javax.net.ssl.SSLSocket;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Сергей on 04.12.2015.
 */
public class Listener implements Runnable {
    private Thread thread;
    private static final int BUFF_LEN = 1024 * 5;
    private InputStream inputStream;
    //private PackageService packageService;

    private ConcurrentLinkedDeque<Package> arrayDeque;

    public Listener(SSLSocket sslSocket, ConcurrentLinkedDeque<Package> arrayDeque){
        try {
            inputStream = sslSocket.getInputStream();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.arrayDeque = arrayDeque;

        //packageService = new PackageService();
        thread = new Thread(this, "ListenerThread");
        thread.start();
    }

    public void run() {
        listen();
    }

    private void listen() {
        try {
            byte[] buf = new byte[BUFF_LEN];
            int r = 0;
            String request;

            while (true) {
                //System.out.println("kkk");
                try {
                    if ((r = inputStream.read(buf)) > 0) {
                        try {
                            //System.out.println("eee");
                            Package receivedPack = Package.deserialize(buf);
                            //packageService.processPackage(receivedPack);
                            arrayDeque.add(receivedPack);
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
}
