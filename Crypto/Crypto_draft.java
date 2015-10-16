package ru.JMessenger.crypto;
import java.math.BigInteger;
import java.util.Random;

/**
 * @author Сергей
 */
public class Authentication {
    private final BigInteger a;
    private final BigInteger p;
    private final BigInteger x;
    private final BigInteger y;

    private BigInteger c;
    private BigInteger r;
    private BigInteger y2;
    private boolean expectedAnswer;

    Authentication(final BigInteger[] init) {
        a = init[0];
        p = init[1];
        x = init[2];
        y = powAXmodP(a, x, p);
    }

    final public BigInteger returnY() {
        return y;
    }
    final public void getY(final BigInteger answer) {
        y2 = answer;
    }

    final public BigInteger returnC() {
        generateNewR();
        return powAXmodP(a, r, p);
    }
    final public void getC(final BigInteger answer) {
        c = answer;
    }

    private void generateNewR() {
        int numBits = 64;
        Random rand = new Random();
        r = (new BigInteger(numBits, rand)).mod(p);
    }

    /**
     * false =>  r;
     * true => (x+r) mod (p-1);
     */
    final public boolean sendRequest() {
        expectedAnswer  = Math.random() < 0.5;
        System.out.print("Victor conceived " + expectedAnswer + " ");
        return expectedAnswer;
    }

    /**
     * if (bool == false) => return r;
     * if (bool == true) => return (x + r) mod (p - 1);
     */
    private BigInteger answerRequest(final boolean request) {
        if (!request) {
            return r;
        } else {
            return r.add(x).mod(p.subtract(BigInteger.ONE));
        }
    }

    public boolean checkAnsver(final BigInteger answer) {
        if (!expectedAnswer) {
            return equals(powAXmodP(a, answer, p), c);
        } else {
            return equals(powAXmodP(a, answer, p), y2.multiply(c).mod(p));
        }
    }

    /**
     *
     * @param a -- base
     * @param x -- exponent
     * @param p -- module
     * @return a ^ x mod p
     */
    static private BigInteger powAXmodP(BigInteger a, BigInteger x, BigInteger p) {
        BigInteger result = new BigInteger("1");

        // BigInteger.TWO ?
        BigInteger two = new BigInteger("2");
        BigInteger zero = new BigInteger("0");

        while (x.compareTo(zero) != 0)  {
            if (x.mod(two).compareTo(zero) == 1) {
                result = result.multiply(a).mod(p);
            }
            a = a.multiply(a).mod(p);
            x = x.divide(two).mod(p);
        }

        return result;
    }

    /**
     *
     * @param x
     * @param y
     * @return boolean
     */
    private static boolean equals(final BigInteger x, final BigInteger y) {
        return x.compareTo(y) == 0;
    }

    /**
     *
     * @param args
     */
    public static void main(final String[] args) {
        BigInteger pow2in64 = new BigInteger("18446744073709551616");

        BigInteger[] arrayPeggy =
                {new BigInteger("654124187867"), new BigInteger("654124187881"), new BigInteger("654124188353")};
        BigInteger[] arrayVictor =
                {new BigInteger("654124187867"), new BigInteger("654124187881"), new BigInteger("384490349669")};

        Authentication Peggy  = new Authentication(arrayPeggy);
        Authentication Victor  = new Authentication(arrayVictor);


        //Виктор и Пегги обмениваются ключами
        Peggy.getY(Victor.returnY());
        Victor.getY(Peggy.returnY());

        for (int i = 0; i < 20; ++i) {
            //Виктор получает от Пегги С
            Victor.getC(Peggy.returnC());

            //Виктор выбирает из {false,true} и посылает Пегги
            //Пегги принимает запрос и в зависимости от этого отвечает
            //r или (x+r)mod(p-1)
            //Виктор проверяет
            if (Victor.checkAnsver(Peggy.answerRequest(Victor.sendRequest()))) {
                System.out.println("Correct");
            } else {
                throw new RuntimeException("Peggy gave the wrong answer!");
            }
        }

    }
}
