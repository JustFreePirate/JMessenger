package ru.jmessenger.application;

import javax.net.ssl.SSLSocket;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Сергей on 04.12.2015.
 */
public class Sender {
    private OutputStream outputStream;
    public Sender (SSLSocket sslSocket) {
        try {
            outputStream = sslSocket.getOutputStream();
        }
        catch (Exception e) {
            //
        }
    }

}
