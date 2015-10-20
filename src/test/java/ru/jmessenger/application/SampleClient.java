package ru.jmessenger.application;

/**
 * Created by Дмитрий on 18.10.2015.
 */
import java.io.*;
import java.net.*;

class SampleClient extends Thread {
    public static void main(String args[]) {
        try {
            // открываем сокет и коннектимся к localhost:3128
            // получаем сокет сервера
            Socket s = new Socket("213.21.24.143", 5000);
            //Socket s2 = new Socket("localhost", 3128);

            String header = "GET http://213.21.24.143/ HTTP/1.1\n" +
                    "Host: http://213.21.24.143/\n" +
                    "User-Agent: HTTPClient\n\n";
            // берём поток вывода и выводим туда первый аргумент
            // заданный при вызове, адрес открытого сокета и его порт
            System.out.println("1");
            s.getOutputStream().write(header.getBytes());
            //s2.getOutputStream().write(s2.getLocalSocketAddress().toString().getBytes());
            System.out.println("2");

            // читаем ответ
            byte buf[] = new byte[64 * 1024];
            //byte buf2[] = new byte[64*1024];
            int r = s.getInputStream().read(buf);
            System.out.println("3");
            //int r2 = s2.getInputStream().read(buf2);
            String data = new String(buf, 0, r);
//            String data2 = new String(buf2, 0, r2);
            // выводим ответ в консоль
            System.out.println(data);
//            System.out.println(data2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
