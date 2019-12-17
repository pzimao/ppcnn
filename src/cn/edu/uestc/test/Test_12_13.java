package cn.edu.uestc.test;

import cn.edu.uestc.paillier.Paillier;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public class Test_12_13 {

    public static void main(String[] args) throws Exception {

        BigInteger num1 = new BigInteger("2");
        BigInteger num2 = new BigInteger("-3");
        BigInteger num3 = new BigInteger("-2");
        BigInteger num4 = new BigInteger("3324325345436546435435234532423432534534543534543");
        System.out.println(num1.modPow(num3, num4));
        Paillier paillier = new Paillier(64, 64);

        long start, end;
        start = System.nanoTime();
        for (int i =0;i<100000;i++) {
            new Random();
        }
        end = System.nanoTime();
        System.out.println(end - start);

        start = System.nanoTime();
        for (int i =0;i<11000000;i++) {
            num3.add(num4);
        }
        end = System.nanoTime();
        System.out.println(end - start);

    }
}
