package ru.jmessenger.crypto;
import java.math.BigInteger;
import java.util.Random;

/**
 * @author Сергей
 */
public final class ManagerAuthentication implements InterfaceManagerAuthentication {
    private final BigInteger publicBase;
    private final BigInteger publicModulo;
    private final BigInteger ownSecretKey;
    private final BigInteger ownPublicKey;

    private BigInteger c;
    private BigInteger r;
    private BigInteger foreignPublicKey;
    private boolean expectedAnswer;

    ManagerAuthentication(final BigInteger[] args) {
        publicBase = args[0];
        publicModulo = args[1];
        ownSecretKey = args[2];

        ownPublicKey =
                InterfaceManagerAuthentication.fastModularExponentiation(publicBase, ownSecretKey, publicModulo);
    }

    public BigInteger returnOwnPublicKey() {
        return ownPublicKey;
    }
    public void getForeignPublicKey(final BigInteger answer) {
        foreignPublicKey = answer;
    }

    public BigInteger returnC() {
        generateNewR();
        return InterfaceManagerAuthentication.fastModularExponentiation(publicBase, r, publicModulo);
    }
    public void getC(final BigInteger answer) {
        c = answer;
    }

    private void generateNewR() {
        final int numBits = 64;
        Random rand = new Random();
        r = (new BigInteger(numBits, rand)).mod(publicModulo);
    }

    /**
     * false =>  r;
     * true => (x+r) mod (p-1);
     */
    public boolean sendRequest() {
        expectedAnswer  = Math.random() < 0.5;
        System.out.print("Victor conceived " + expectedAnswer + " ");
        return expectedAnswer;
    }

    /**
     * if (bool == false) => return r;
     * if (bool == true) => return (x + r) mod (p - 1);
     */
    public BigInteger answerRequest(final boolean request) {
        if (!request) {
            return r;
        } else {
            return r.add(ownSecretKey).mod(publicModulo.subtract(BigInteger.ONE));
        }
    }

    public boolean checkAnswer(final BigInteger answer) {
        if (!expectedAnswer) {
            return InterfaceManagerAuthentication.equals(
                    InterfaceManagerAuthentication.fastModularExponentiation(publicBase, answer, publicModulo),
                    c
            );
        } else {
            return InterfaceManagerAuthentication.equals(
                    InterfaceManagerAuthentication.fastModularExponentiation(publicBase, answer, publicModulo),
                    foreignPublicKey.multiply(c).mod(publicModulo)
            );
        }
    }

    public static void main(final String[] args) {
        ManagerAuthentication autPeggy  = new ManagerAuthentication(
                new BigInteger[]{
                        new BigInteger("11111111111111111111111"),
                        new BigInteger("19175002942688032928599"),
                        new BigInteger("18014398241046527")});

        ManagerAuthentication autVictor  = new ManagerAuthentication(
                new BigInteger[] {
                        new BigInteger("11111111111111111111111"),
                        new BigInteger("19175002942688032928599"),
                        new BigInteger("1125899839733759")});


        //Виктор и Пегги обмениваются ключами
        autPeggy.getForeignPublicKey(autVictor.returnOwnPublicKey());
        autVictor.getForeignPublicKey(autPeggy.returnOwnPublicKey());

        for (int i = 0; i < 25; ++i) {
            //Виктор получает от Пегги С
            autVictor.getC(autPeggy.returnC());

            //Виктор выбирает из {false,true} и посылает Пегги
            //Пегги принимает запрос и в зависимости от этого отвечает
            //r или (x+r)mod(p-1)
            //Виктор проверяет
            if (autVictor.checkAnswer(autPeggy.answerRequest(autVictor.sendRequest()))) {
                System.out.println("Correct");
            } else {
                throw new RuntimeException("Peggy gave the wrong answer!");
            }
        }

    }
}
