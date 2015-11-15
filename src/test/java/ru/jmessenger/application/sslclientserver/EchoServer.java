package ru.jmessenger.application.sslclientserver;

/**
 * Created by dima on 06.11.15.
 */
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.security.KeyStore;

public
class EchoServer {
    private static String ksName = "src/res/server_key_store.jks";
    //private static char[] ksPass = "free240195".toCharArray();
    private static char[] crtPass = "free240195".toCharArray();
    public static void main(String[] args) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(ksName), null);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, crtPass);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(8888);
            printServerSocketInfo(serverSocket);
            SSLSocket sslSocket = (SSLSocket) serverSocket.accept();
            printSocketInfo(sslSocket);

            InputStream inputstream = sslSocket.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                System.out.println(string);
                System.out.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: "+s.getClass());
        System.out.println("   Remote address = "
                +s.getInetAddress().toString());
        System.out.println("   Remote port = "+s.getPort());
        System.out.println("   Local socket address = "
                +s.getLocalSocketAddress().toString());
        System.out.println("   Local address = "
                +s.getLocalAddress().toString());
        System.out.println("   Local port = "+s.getLocalPort());
        System.out.println("   Need client authentication = "
                +s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        System.out.println("   Cipher suite = "+ss.getCipherSuite());
        System.out.println("   Protocol = "+ss.getProtocol());
    }

    private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: "+s.getClass());
        System.out.println("   Socker address = "
                +s.getInetAddress().toString());
        System.out.println("   Socker port = "
                +s.getLocalPort());
        System.out.println("   Need client authentication = "
                +s.getNeedClientAuth());
        System.out.println("   Want client authentication = "
                +s.getWantClientAuth());
        System.out.println("   Use client mode = "
                +s.getUseClientMode());
    }
}