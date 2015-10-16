package ru.JMessenger.crypto;
import java.math.BigInteger;

/**
 * @author Сергей
 */
public class Crypto_draft {
    final BigInteger a;
    final BigInteger p;
    final BigInteger x;

    Crypto_draft(BigInteger[] init){
        a = init[0];
        p = init[1];
        x = init[2];
    }

    /**
     *
     * @return int
     */
    public BigInteger getY () {
        return powAXmodP(a,x,p);
    }

    //static, private ?
    static private BigInteger powAXmodP (BigInteger a, BigInteger x, BigInteger p){
        BigInteger result = new BigInteger("1");

        // BigInteger.TWO ?
        BigInteger TWO = new BigInteger("2");
        BigInteger ZERO = new BigInteger("0");

        while (x.compareTo(ZERO) != 0)  {
            if(x.mod(TWO).compareTo(ZERO) == 1){
                result = result.multiply(a).mod(p);
            }
            a = a.multiply(a).mod(p);
            x = x.divide(TWO).mod(p);
        }

        return result;
    }


    public static void main(String[] args) {
        BigInteger pow_2_64 = new BigInteger("18446744073709551616");

        BigInteger[] array1 =
                {new BigInteger("21"), new BigInteger("19"), new BigInteger("13")};

        BigInteger[] array2 =
                {new BigInteger("654124187867"), new BigInteger("654124187881"), new BigInteger("654124188353")};

        Crypto_draft test = new Crypto_draft(array2);


        System.out.println(test.getY());
        System.out.println(test.getY());

    }
}
