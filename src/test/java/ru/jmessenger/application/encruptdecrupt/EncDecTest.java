package ru.jmessenger.application.encruptdecrupt;

import com.sun.crypto.provider.AESKeyGenerator;
import javafx.util.converter.ByteStringConverter;

import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by dima on 06.11.15.
 */
public class EncDecTest {
    public static void main(String[] args) throws Exception{
        /*byte[] input = "my secret message".getBytes();
        byte[] keyBytes = "my super puper secret key string".getBytes();
        byte[] ivBytes = "init vec".getBytes();
        System.out.println(new String(input));

        DESKeySpec dkey = new DESKeySpec(keyBytes);
        AESKeyGenerator aesKeyGenerator = new AESKeyGenerator();


        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrupted = cipher.doFinal(input);

        System.out.println(new String(encrupted));*/

//        SSLSocket socket = SSLSocketFactory.getDefault().createSocket();
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        System.out.println(Arrays.toString(sslSocketFactory.getDefaultCipherSuites()));
    }

}
