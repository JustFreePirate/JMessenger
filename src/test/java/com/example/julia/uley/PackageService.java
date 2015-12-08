package com.example.julia.uley;

import com.example.julia.uley.common.PackageType;
import com.example.julia.uley.common.Package;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Сергей on 07.12.2015.
 *
 * class for tests
 */
public class PackageService implements Runnable {
    private Thread thread;
//    private ArrayDeque<Package> arrayDeque;
    private ConcurrentLinkedDeque<Package> arrayDeque;
    SimpleDateFormat formatter;

    public PackageService(ConcurrentLinkedDeque<Package> arrayDeque){
        formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

        this.arrayDeque = arrayDeque;

        thread = new Thread(this, "PackageServiceThread");
        thread.start();
    }

    public void run() {
        processPackage();
    }

    public Package getPackage() {
        if (arrayDeque.isEmpty()) return null;

        Package pack =  arrayDeque.getFirst();
        arrayDeque.removeFirst();
        return  pack;
    }

    private void processPackage() {
        while (true) {

            Package pack = getPackage();
            if (pack == null) continue;

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
}
