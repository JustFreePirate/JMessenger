package ru.jmessenger.crypto;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Сергей on 18.10.2015.
 */
public interface InterfaceManagerAuthentication {
    BigInteger returnOwnPublicKey();
    void getForeignPublicKey(final BigInteger answer);

    BigInteger returnC();

    void getC(final BigInteger answer);

    /**
     * false =>  r;
     * true => (x+r) mod (p-1);
     */
    boolean sendRequest();

    /**
     * if (bool == false) => return r;
     * if (bool == true) => return (x + r) mod (p - 1);
     */
    BigInteger answerRequest(final boolean request);

    boolean checkAnswer(final BigInteger answer);

    static  BigInteger fastModularExponentiation(final BigInteger base, final BigInteger exponent, final BigInteger modulo) {
        // a^x mod p
        BigInteger a = base;
        BigInteger x = exponent;
        BigInteger p = modulo;

        BigInteger result = new BigInteger("1");

        BigInteger two = new BigInteger("2");
        BigInteger zero = new BigInteger("0");

        while (!equals(x,zero))  {
            if (!equals(x.mod(two), zero)) {
                result = result.multiply(a).mod(p);
            }
            a = a.multiply(a).mod(p);
            x = x.divide(two);
        }
        return result;
    }

    static boolean equals(final BigInteger x, final BigInteger y) {
        return x.compareTo(y) == 0;
    }

}
