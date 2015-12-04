package ru.jmessenger.application;

import ru.jmessenger.application.common.*;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Сергей on 04.12.2015.
 */
public class Sender {
    private OutputStream outputStream;
    //private OutputStreamWriter outputStreamWriter;

    public Sender (SSLSocket sslSocket) {
        try {
            outputStream = sslSocket.getOutputStream();
            //outputStreamWriter = new OutputStreamWriter(outputStream);
        }
        catch (Exception e) {
            //
        }
    }

    public void sendPackage(ru.jmessenger.application.common.Package aPackage) throws IOException {
        byte[] serialized = aPackage.serialize();
        System.out.println("length of package: " + serialized.length);
        outputStream.write(serialized, 0, serialized.length);
        outputStream.flush();
    }

}
