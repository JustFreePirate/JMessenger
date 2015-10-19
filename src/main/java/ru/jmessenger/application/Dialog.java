package ru.jmessenger.application;

import ru.jmessenger.application.listeners.OnGotAMessageListener;

import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Дмитрий on 19.10.2015.
 * Диалог между двумя клиентами.
 *
 */
public class Dialog extends Thread {
    private static final int REFRESH_TIMEOUT_MS = 500;
    private final Socket socket;
    private final OnGotAMessageListener listener;

    Dialog(Socket socket, OnGotAMessageListener listener) {
        System.out.println("New Dialog with: " + socket.getInetAddress().getHostName() + ":" + socket.getPort());
        this.socket = socket;
        this.listener = listener;
        sendMessage("Succeed connection to Server: " + socket.getLocalAddress().getHostName());
        setPriority(NORM_PRIORITY);
        start();
    }

    @Override
    public void run() {
        //слушаем что нам приходит
        //если пришло -> listener.onGotAMessage(message)
        try {
            InputStream is = socket.getInputStream();
            byte buf[] = new byte[64];
            while (true) {
                int r = is.read(buf);
                while (r > 0) {
                    listener.onGotAMessage(new String(buf, 0, r));
                    r = is.read(buf);
                }
                sleep(REFRESH_TIMEOUT_MS);
            }
        } catch (Exception e) {
            System.out.println("Failed read from: " + socket.getInetAddress().getHostName());
            Client.getInstance().delDialog(this);
        }
    }

    public void sendMessage(String message) {
        try {
            socket.getOutputStream().write(message.getBytes());
        } catch (Exception e) {
            System.out.println("Failed writing to: " + socket.getInetAddress().getHostName());
            Client.getInstance().delDialog(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Dialog)) {
            return false;
        }
        Dialog dialog = (Dialog) obj;
        return socket.equals(dialog.socket);
    }

    @Override
    protected void finalize() throws Throwable {
        socket.close();
    }
}
