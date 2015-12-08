package com.example.julia.uley;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Сергей on 04.12.2015.
 */
public class Sender {
    private OutputStream outputStream;

    public Sender(SSLSocket sslSocket) {
        try {
            outputStream = sslSocket.getOutputStream();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendPackage(com.example.julia.uley.common.Package aPackage) throws IOException {
        byte[] serialized = aPackage.serialize();
        System.out.println("length of package: " + serialized.length);
        outputStream.write(serialized, 0, serialized.length);
        outputStream.flush();
    }

}
